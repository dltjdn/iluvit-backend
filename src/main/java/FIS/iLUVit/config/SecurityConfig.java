package FIS.iLUVit.config;

import FIS.iLUVit.filter.CorsConfig;
import FIS.iLUVit.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    private final CorsFilter corsFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        // JWT를 사용할거기 때문에 STATELESS 즉, 세션을 사용하지 않겠다.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .addFilter(corsFilter)
                .formLogin().disable()  // springSecurity가 제공하는 formLogin 기능 사용X
                .httpBasic().disable()  // 매 요청마다 id, pwd 보내는 방식으로 인증하는 httpBasic 사용X
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .authorizeRequests()
                .anyRequest().permitAll();
    }
}
