package FIS.iLUVit.domain.user.service;

import FIS.iLUVit.domain.alarm.service.AlarmService;
import FIS.iLUVit.domain.authnum.domain.AuthNumber;
import FIS.iLUVit.domain.authnum.exception.AuthNumberErrorResult;
import FIS.iLUVit.domain.authnum.exception.AuthNumberException;
import FIS.iLUVit.domain.authnum.repository.AuthRepository;
import FIS.iLUVit.domain.blackuser.domain.BlackUser;
import FIS.iLUVit.domain.blackuser.exception.BlackUserErrorResult;
import FIS.iLUVit.domain.blackuser.exception.BlackUserException;
import FIS.iLUVit.domain.blackuser.repository.BlackUserRepository;
import FIS.iLUVit.domain.expotoken.repository.ExpoTokenRepository;
import FIS.iLUVit.domain.scrap.domain.Scrap;
import FIS.iLUVit.domain.scrap.repository.ScrapRepository;
import FIS.iLUVit.domain.scrap.service.ScrapService;
import FIS.iLUVit.domain.tokenpair.repository.TokenPairRepository;
import FIS.iLUVit.domain.tokenpair.domain.TokenPair;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.dto.*;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.tokenpair.dto.TokenRefreshRequest;
import FIS.iLUVit.domain.authnum.domain.AuthKind;
import FIS.iLUVit.domain.blackuser.domain.UserStatus;
import FIS.iLUVit.global.security.JwtUtils;
import FIS.iLUVit.global.security.uesrdetails.PrincipalDetails;
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
    private final AuthRepository authRepository;
    private final TokenPairRepository tokenPairRepository;
    private final ExpoTokenRepository expoTokenRepository;
    private final ScrapRepository scrapRepository;
    private final ScrapService scrapService;
    private final AlarmService alarmService;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final BlackUserRepository blackUserRepository;

    /**
     * 유저 기본정보( id, nickname, auth )를 반환합니다
     */
    public UserFindOneResponse findUserDetails(Long userId) {
        // 블랙 유저 검증
        blackUserRepository.findByUserId(userId)
                .ifPresent(blackUser -> {
                            throw new BlackUserException(BlackUserErrorResult.USER_IS_BLACK_OR_WITHDRAWN);
                });
        // 유저 id로 유저 조회
        User user = getUser(userId);

        // 유저의 기본 정보 반환
        return user.getUserInfo();
    }



    /**
     * 중복된 로그인 아이디일 경우 에러를 반환합니다
     */
    public void checkLoginIdAvailability(UserCheckDuplicateLoginIdRequest request) {
        String loginId = request.getLoginId();
        Optional<BlackUser> blackUser = blackUserRepository.findByLoginId((loginId));
        Optional<User> user = userRepository.findByLoginId(loginId);

        // 블랙 유저나 유저에 있는 로그인 아이디면 가입불가
        if (blackUser.isPresent() || user.isPresent()) {
            throw new UserException(UserErrorResult.DUPLICATE_LOGIN_ID);
        }
    }

    /**
     * 중복된 닉네임일 경우 에러를 반환힙니다
     */
    public void checkNicknameAvailability(UserCheckDuplicateNicknameRequest request) {
        String nickname = request.getNickname();
        Optional<BlackUser> blackUser = blackUserRepository.findByNickName(nickname);
        Optional<User> user = userRepository.findByNickName(nickname);

        // 블랙 유저나 유저에 있는 닉네임이면 가입불가
        if (blackUser.isPresent() || user.isPresent()) {
            throw new UserException(UserErrorResult.DUPLICATE_NICKNAME);
        }
    }

    /**
     * 비밀번호를 변경합니다
     */
    public void changePassword(Long id, UserPasswordUpdateRequest request) {
        // 유저 id로 유저 정보 조회
        User findUser = getUser(id);

        // 기존 비밀번호와 유저가 입력한 현재 비밀번호를 확인
        if (!encoder.matches(request.getOriginPwd(), findUser.getPassword())) {
            throw new UserException(UserErrorResult.INCORRECT_PASSWORD);
        }
        // 새 비밀번호 확인
        if (!request.getNewPwd().equals(request.getNewPwdCheck())) {
            throw new UserException(UserErrorResult.PASSWORD_CHECK_MISMATCH);
        }

        // 비밀번호 변경
        findUser.changePassword(encoder.encode(request.getNewPwd()));
    }

    /**
     * 유저의 로그인 요청을 처리합니다
     */
    public UserLoginResponse login(UserLoginRequest request) {
        // 영구정지, 일주일간 이용제한 유저인지 검증
        blackUserRepository.findRestrictedByLoginId(request.getLoginId())
                .ifPresent(blackUser -> {
                    throw new BlackUserException(BlackUserErrorResult.USER_IS_BLACK_OR_WITHDRAWN);
                });

        // 아이디 및 비밀번호 확인을 위해 authenticationManager를 사용하여 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword()));

        // 인증된 객체 생성
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        // JWT 생성
        String jwt = jwtUtils.createAccessToken(authentication);
        String refresh = jwtUtils.createRefreshToken(authentication);
        TokenPair tokenPair = TokenPair.createTokenPair(jwt, refresh, principal.getUser());

        // 기존 토큰이 있으면 업데이트하고, 없으면 새로 생성하여 저장
        tokenPairRepository.findByUser(principal.getUser())
                .ifPresentOrElse(
                        (findTokenPair) -> findTokenPair.updateToken(jwt, refresh),
                        () -> tokenPairRepository.save(tokenPair)
                );

        // 응답에 필요한 유저 정보 생성
        UserLoginResponse response = principal.getUser().getLoginInfo();
        response.setAccessToken(jwtUtils.addPrefix(jwt));
        response.setRefreshToken(jwtUtils.addPrefix(refresh));

        // 튜토리얼 비활성화 처리
        principal.getUser().disableTutorial();

        return response;
    }

    /**
     * refreshToken으로 accessToken를 재발급합니다
     */
    public UserLoginResponse refreshAccessToken(TokenRefreshRequest request) {
        //요청으로 받은 refreshToken 추출
        String requestRefreshToken = request.getRefreshToken().replace("Bearer ", "");

        // 요청으로 받은 refreshToken의 유효성 확인
        jwtUtils.validateToken(requestRefreshToken);

        // 이전에 받았던 refreshToken과 일치하는지 확인(tokenPair 유저당 하나로 유지)
        Long userId = jwtUtils.getUserIdFromToken(requestRefreshToken);
        User user = getUser(userId);
        TokenPair findTokenPair = tokenPairRepository.findByUser(user)
                .orElseThrow(() -> new JWTVerificationException("유효하지 않은 토큰입니다."));
        if (!requestRefreshToken.equals(findTokenPair.getRefreshToken())) {
            throw new JWTVerificationException("중복 로그인 되었습니다.");
        }

        // 이전에 발급한 AccessToken이 만료되었다면 refreshToken을 사용하여 갱신
        if (jwtUtils.isExpired(findTokenPair.getAccessToken())) {
            // refreshToken 유효하고, AccessToken 정상적으로 Expired 상태일때
            PrincipalDetails principal = new PrincipalDetails(findTokenPair.getUser());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            String jwt = jwtUtils.createAccessToken(authentication);
            String refresh = jwtUtils.createRefreshToken(authentication);
            findTokenPair.updateToken(jwt, refresh);

            UserLoginResponse response = principal.getUser().getLoginInfo();
            response.setAccessToken(jwtUtils.addPrefix(jwt));
            response.setRefreshToken(jwtUtils.addPrefix(refresh));

            return response;

        } else {
            // 만료되지 않은 AccessToken인 경우, 토큰 탈취로 판단하여 tokenPair 삭제
            tokenPairRepository.delete(findTokenPair);
            throw new JWTVerificationException("유효하지 않은 시도입니다.");
        }
    }

    /**
     * 입력받은 비밀번호를 해싱하여 유효성을 검사하고, 회원가입에 필요한 검증을 수행합니다 ( 교사, 학부모 공통 )
     */
    public String hashAndValidatePwdForSignup(String password, String passwordCheck, String loginId, String phoneNum, String nickName) {
        // 비밀번호 확인
        if (!password.equals(passwordCheck)) {
            throw new UserException(UserErrorResult.PASSWORD_CHECK_MISMATCH);
        }

        // 로그인 아이디, 닉네임 중복확인
        userRepository.findByLoginIdOrNickName(loginId, nickName)
                .ifPresent((user) ->{
                    throw new UserException(UserErrorResult.DUPLICATE_NICKNAME);
                });

        // 핸드폰 인증확인
        AuthNumber authComplete = authRepository.findByPhoneNumAndAuthKindAndAuthTimeNotNull(phoneNum, AuthKind.signup)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.PHONE_NUMBER_UNVERIFIED));

        // 핸드폰 인증 후 지정된 시간이 지나면 인증 무효
        if (Duration.between(authComplete.getAuthTime(), LocalDateTime.now()).getSeconds() > (60 * 60)) {
            throw new AuthNumberException(AuthNumberErrorResult.AUTH_NUMBER_EXPIRED);
        }

        return encoder.encode(password);
    }

    /**
     * 회원 탈퇴 요청을 처리합니다 ( 교사, 학부모 공통 )
     */
    public void withdrawUser(Long userId){
        // 유저 정보 삭제 & 게시글, 댓글, 채팅, 시설리뷰 작성자 '알 수 없음'
        User user = getUser(userId);
        // 15일 동안 재가입 방지를 위해 블랙 유저에 저장
        blackUserRepository.save(new BlackUser(user, UserStatus.WITHDRAWN));

        // id 제외 유저 정보 삭제
        user.deletePersonalInfo();

        // 스크랩 폴더 삭제 -> 스크랩한 포스트 casecade 됨
        List<Scrap> scrapDirs = scrapRepository.findByUser(user);

        // 스크랩 폴더 삭제 -> 스크랩한 포스트 casecade 됨
        scrapDirs.forEach(scrapDir -> {
            if(!scrapDir.getIsDefault()){
                scrapService.deleteScrapDir(userId, scrapDir.getId());
            }
        });

        //유저 알람 전체 삭제
        alarmService.deleteAllAlarm(userId);
        //유저의 expoToken 모두 삭제
        expoTokenRepository.deleteAllByUser(user);
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }
}
