package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.RefreshToken;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.repository.RefreshTokenRepository;
import FIS.iLUVit.security.JwtUtils;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.security.uesrdetails.PrincipalDetails;
import FIS.iLUVit.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 사용자 기본정보(id, nickname, auth)반환
     */
    @GetMapping("/user/info")
    public UserInfoResponse findUserInfo(@Login Long id) {
        return userService.findUserInfo(id);
    }

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 비밀번호 변경
     */
    @PutMapping("/user/password")
    public void updatePassword(@Login Long id, @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
    }

    @GetMapping("/alarm")
    public Slice<AlarmDto> getAlarm(@Login Long userId, Pageable pageable) {
        return userService.findUserAlarm(userId, pageable);
    }

    @DeleteMapping("/alarm")
    public Integer deleteAlarm(@Login Long userId, @RequestBody AlarmDeleteDto request) {
        return userService.deleteUserAlarm(userId, request.getAlarmId());
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshTokenToken = request.getRefreshToken().replace("Bearer ", "");

        jwtUtils.validateToken(requestRefreshTokenToken);

        Long userId = jwtUtils.getUserIdFromToken(requestRefreshTokenToken);

        RefreshToken findRefreshToken = refreshTokenRepository.findByUserIdWithUser(userId)
                .orElseThrow(() -> new JWTVerificationException("유효하지 않은 토큰입니다."));

        if (!request.getRefreshToken().equals(findRefreshToken.getToken())) {
            throw new JWTVerificationException("유효하지 않은 토큰입니다.");
        }

        PrincipalDetails principal = new PrincipalDetails(findRefreshToken.getUser());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String jwt = jwtUtils.createAccessToken(authentication);
        String refresh = jwtUtils.createRefreshToken(authentication);
        findRefreshToken.updateToken(refresh);


        LoginResponse response = principal.getUser().getLoginInfo();
        response.setJwt(jwt);
        response.setRefresh(refresh);
        return response;
    }
}
