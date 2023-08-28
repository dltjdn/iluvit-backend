package FIS.iLUVit.config;

import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.security.ExceptionHandlerFilter;
import FIS.iLUVit.security.JwtAuthorizationFilter;
import FIS.iLUVit.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final CorsConfig corsConfig;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        // JWT를 사용할거기 때문에 STATELESS 즉, 세션을 사용하지 않겠다.
        http.addFilter(corsConfig.corsFilter())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()  // springSecurity가 제공하는 formLogin 기능 사용X
                .httpBasic().disable()  // 매 요청마다 id, pwd 보내는 방식으로 인증하는 httpBasic 사용X
                .addFilter(corsConfig.corsFilter())
                .addFilter(jwtAuthorizationFilter())
                .addFilterBefore(exceptionHandlerFilter(), LogoutFilter.class)
                .exceptionHandling()
                    .accessDeniedHandler(accessDeniedHandler)
                    .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/teacher/search/center/**", "/teacher/signup/**").permitAll()
                .antMatchers("/teacher/**").hasAnyRole("TEACHER", "DIRECTOR")
                .antMatchers("/parent/signup/**").permitAll()
                .antMatchers("/parent/**","/participation/**","/waiting/**","/center-bookmark/**").hasRole("PARENT")
                .antMatchers(HttpMethod.PATCH,"/expo-tokens/deactivate").permitAll()
                .antMatchers("/alarm/**", "/board-bookmark/**", "/board/**","/center/**", "/chat/**", "/child/**", "/comment/**", "/comment-heart/**", "/expo-token/**"
                        , "/post/**", "/post-heart/**", "/presentation/**", "/report/**", "/review/**", "/review-heart/**", "/scrap/**", "/user/**", "/password/**")
                .hasAnyRole("TEACHER", "DIRECTOR", "PARENT")
                .anyRequest().permitAll();
        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(authenticationManagerBean(), userRepository, jwtUtils);
    }

    @Bean
    public ExceptionHandlerFilter exceptionHandlerFilter() {
        return new ExceptionHandlerFilter();
    }
}
