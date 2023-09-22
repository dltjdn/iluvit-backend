package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.domain.enumtype.UserStatus;
import FIS.iLUVit.dto.user.*;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.security.JwtUtils;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.security.uesrdetails.PrincipalDetails;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final TokenPairRepository tokenPairRepository;
    private final ScrapRepository scrapRepository;
    private final ScrapService scrapService;
    private final ExpoTokenRepository expoTokenRepository;
    private final AlarmService alarmService;
    private final BlackUserRepository blackUserRepository;
    private final BoardBookmarkService boardBookmarkService;

    /**
     * 작성자: 이승범
     * 작성내용: 사용자 기본정보(userId, nickname, auth) 반환
     */
    public UserResponse findUserDetails(Long userId) {
        // 블랙 유저 검증
        blackUserRepository.findByUserId(userId)
                .ifPresent(blackUser -> {
                    throw new UserException(UserErrorResult.USER_IS_BLACK_OR_WITHDRAWN);
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        return user.getUserInfo();
    }

    /**
    *   작성자: 이승범
    *   작성내용: 비밀번호 변경
    */
    public User changePassword(Long id, PasswordRequest request) {

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        if (!bCryptPasswordEncoder.matches(request.getOriginPwd(), findUser.getPassword())) {
            throw new SignupException(SignupErrorResult.NOT_MATCH_PWD);
        } else if (!request.getNewPwd().equals(request.getNewPwdCheck())) {
            throw new SignupException(SignupErrorResult.NOT_MATCH_PWDCHECK);
        }

        findUser.changePassword(bCryptPasswordEncoder.encode(request.getNewPwd()));

        return findUser;
    }

    // 회원가입 학부모, 교사 공통 로직(유효성 검사 및 비밀번호 해싱)
    public String hashAndValidatePwdForSignup(String password, String passwordCheck, String loginId, String phoneNum, String nickName) {

        // 비밀번호 확인
        if (!password.equals(passwordCheck)) {
            throw new SignupException(SignupErrorResult.NOT_MATCH_PWDCHECK);
        }

        // 로그인 아이디, 닉네임 중복확인
        User duplicatedUser = userRepository.findByLoginIdOrNickName(loginId, nickName).orElse(null);
        if (duplicatedUser != null) {
            throw new SignupException(SignupErrorResult.DUPLICATED_NICKNAME);
        }

        // 핸드폰 인증확인
        AuthNumber authComplete = authRepository.findAuthComplete(phoneNum, AuthKind.signup)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_AUTHENTICATION));

        // 핸드폰 인증후 너무 많은 시간이 지났으면 인증 무효
        if (Duration.between(authComplete.getAuthTime(), LocalDateTime.now()).getSeconds() > (60 * 60)) {
            throw new AuthNumberException(AuthNumberErrorResult.EXPIRED);
        }

        return bCryptPasswordEncoder.encode(password);
    }

    /**
     *   작성자: 이승범
     *   작성내용: login service layer로 옮김
     */
    public LoginResponse login(LoginRequest request) {
        String loginId = request.getLoginId();
        String password = request.getPassword();

        // 영구정지, 일주일간 이용제한인 유저인지 검증
        blackUserRepository.findRestrictedByLoginId(loginId)
                .ifPresent(blackUser -> {
                    throw new UserException(UserErrorResult.USER_IS_BLACK_OR_WITHDRAWN);
                });

        // 로그인 아이디, 비밀번호 검증 에러 처리
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserException(UserErrorResult.BAD_LOGIN_OR_PASSWORD));

        if(!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorResult.BAD_LOGIN_OR_PASSWORD);
        }
        // authenticationManager 이용한 아이디 및 비밀번호 확인
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword()));

        String jwt = jwtUtils.createAccessToken(authentication);
        String refresh = jwtUtils.createRefreshToken(authentication);
        TokenPair tokenPair = TokenPair.createTokenPair(jwt, refresh, user);

        // 기존 토큰이 있으면 수정, 없으면 생성
        tokenPairRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        (findTokenPair) -> findTokenPair.updateToken(jwt, refresh),
                        () -> tokenPairRepository.save(tokenPair)
                );

        LoginResponse response = user.getLoginInfo();
        response.setAccessToken(jwtUtils.addPrefix(jwt));
        response.setRefreshToken(jwtUtils.addPrefix(refresh));

        // 더 이상 튜토리얼이 진행되지 않도록 하기
        user.disableTutorial();
        return response;
    }

    /**
     *   작성자: 이승범
     *   작성내용: refreshToken으로 accessToken를 재발급
     */
    public LoginResponse refreshAccessToken(TokenRefreshRequest request) {

        String requestRefreshToken = request.getRefreshToken().replace("Bearer ", "");

        // 요청으로 받은 refreshToken 유효한지 확인
        jwtUtils.validateToken(requestRefreshToken);

        // 이전에 받았던 refreshToken과 일치하는지 확인(tokenPair 유저당 하나로 유지)
        Long userId = jwtUtils.getUserIdFromToken(requestRefreshToken);
        TokenPair findTokenPair = tokenPairRepository.findByUserIdWithUser(userId)
                .orElseThrow(() -> new JWTVerificationException("유효하지 않은 토큰입니다."));

        if (!requestRefreshToken.equals(findTokenPair.getRefreshToken())) {
            throw new JWTVerificationException("중복 로그인 되었습니다.");
        }

        // 이전에 발급했던 AccessToken 만료되지 않았다면 refreshToken 탈취로 판단
        // TokenPair 삭제 -> 다시 로그인 해야됨
        if (jwtUtils.isExpired(findTokenPair.getAccessToken())) {
            // refreshToken 유효하고, AccessToken 정상적으로 Expired 상태일때
            PrincipalDetails principal = new PrincipalDetails(findTokenPair.getUser());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            String jwt = jwtUtils.createAccessToken(authentication);
            String refresh = jwtUtils.createRefreshToken(authentication);
            findTokenPair.updateToken(jwt, refresh);

            LoginResponse response = principal.getUser().getLoginInfo();

            response.setAccessToken(jwtUtils.addPrefix(jwt));
            response.setRefreshToken(jwtUtils.addPrefix(refresh));
            return response;

        } else {
            // accessToken이 아직 만료되지 않은 상태 -> 토큰 탈취로 판단 -> delete tokenPair
            tokenPairRepository.delete(findTokenPair);
            throw new JWTVerificationException("유효하지 않은 시도입니다.");
        }
    }

    /**
    *   작성자: 이승범
    *   작성내용: 로그인아이디 중복 확인
    */
    public void checkLoginIdAvailability(CheckLoginIdRequest request) {

        Optional<BlackUser> blackUser = blackUserRepository.findByLoginId((request.getLoginId()));
        Optional<User> user = userRepository.findByLoginId((request.getLoginId()));

        // 블랙 유저나 유저에 있는 로그인 아이디면 가입불가
        if (blackUser.isPresent() || user.isPresent()) {
            throw new UserException(UserErrorResult.ALREADY_LOGINID_EXIST);
        }

    }

    /**
    *   작성자: 이승범
    *   작성내용: 닉네임 중복 확인
    */
    public void checkNicknameAvailability(CheckNicknameRequest request) {

        Optional<BlackUser> blackUser = blackUserRepository.findByNickName((request.getNickname()));
        Optional<User> user = userRepository.findByNickName((request.getNickname()));

        // 블랙 유저나 유저에 있는 닉네임이면 가입불가
        if (blackUser.isPresent() || user.isPresent()) {
            throw new UserException(UserErrorResult.ALREADY_NICKNAME_EXIST);
        }
    }

    /**
     *   작성자: 이서우
     *   작성내용: 회원 탈퇴 ( 교사, 학부모 공통 )
     */
    public long withdrawUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        // 15일 동안 재가입 방지를 위해 블랙 유저에 저장
        blackUserRepository.save(new BlackUser(user, UserStatus.WITHDRAWN));

        // id 제외 유저 정보 삭제
        user.deletePersonalInfo();

        // 스크랩 폴더 삭제 -> 스크랩한 포스트 casecade 됨
        List<Scrap> scrapDirs = scrapRepository.findByUser(user);

        scrapDirs.forEach(scrapDir -> {
            if(scrapDir.getIsDefault() == false){
                scrapService.deleteScrapDir(userId, scrapDir.getId());
            };
        });

        // 유저 북마크 전체 삭제
        boardBookmarkService.deleteAllBookmark(userId);

        //유저 알람 전체 삭제
        alarmService.deleteAllAlarm(userId);

        //유저의 expoToken 모두 삭제
        expoTokenRepository.deleteAllByUser(user);

        return userId;
    }

}
