package FIS.iLUVit.uesrdetails;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// /login 요청 => formLogin.disable 해놔서 UserDetailService 동작 안함 => UserDetailService 작동시키는 필터 필요(JwtAuthenticationFilter)
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username이 loginId이다.
        User user = userRepository.findByLoginId(username)
                .orElseThrow(()->new UserException("존재하지 않는 아이디입니다."));
        return new PrincipalDetails(user);
    }
}
