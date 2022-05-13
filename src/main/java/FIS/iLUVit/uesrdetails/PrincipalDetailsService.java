package FIS.iLUVit.uesrdetails;

import FIS.iLUVit.domain.User;
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
        User user = userRepository.findByLoginId(username);
        return new PrincipalDetails(user);
    }
}
