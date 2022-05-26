package FIS.iLUVit.config;

import FIS.iLUVit.filter.CustomAuthenticationSuccessHandler;
import FIS.iLUVit.filter.ExceptionHandlerFilter;
import FIS.iLUVit.filter.JwtAuthenticationFilter;
import FIS.iLUVit.filter.JwtAuthorizationFilter;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final CorsConfig corsConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        http.csrf().disable();
        // JWT를 사용할거기 때문에 STATELESS 즉, 세션을 사용하지 않겠다.
        http.addFilter(corsConfig.corsFilter())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()  // springSecurity가 제공하는 formLogin 기능 사용X
                .httpBasic().disable()  // 매 요청마다 id, pwd 보내는 방식으로 인증하는 httpBasic 사용X
                .addFilterBefore(new ExceptionHandlerFilter(), LogoutFilter.class)
                .addFilter(jwtAuthenticationFilter)
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
//                .antMatchers("/user/**")
//                .access("hasRole('PARENT') or hasRole('TEACHER') or hasRole('DIRECTOR')")
                .antMatchers("/parent/**")
                .access("hasRole('PARENT')")
                .anyRequest().permitAll();
    }


    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
}
