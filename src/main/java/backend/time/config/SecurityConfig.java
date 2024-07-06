package backend.time.config;

import backend.time.config.auth.PrincipalDetailService;
import backend.time.config.jwt.JwtRequestFilter;
import backend.time.handler.KakaoLoginFailureHandler;
import backend.time.handler.KakaoLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Autowired
    private PrincipalDetailService principalDetailService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private KakaoLoginSuccessHandler kakaoLoginSuccessHandler;
    @Autowired
    private KakaoLoginFailureHandler kakaoLoginFailureHandler;
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    BCryptPasswordEncoder encode() {
        return new BCryptPasswordEncoder();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(principalDetailService).passwordEncoder(encode());
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .authorizeHttpRequests()
                    .anyRequest()
                    .permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                .loginProcessingUrl("/kakao/login")
                .successHandler(kakaoLoginSuccessHandler)
                .failureHandler(kakaoLoginFailureHandler);

        return http.build();
    }
}