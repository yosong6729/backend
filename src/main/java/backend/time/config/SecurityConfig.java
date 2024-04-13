package backend.time.config;

import backend.time.config.auth.PrincipalDetailService;
import backend.time.config.jwt.JwtRequestFilter;
import backend.time.config.jwt.JwtTokenUtil;
import backend.time.dto.MemberDto;
import backend.time.handler.KakaoLoginFailureHandler;
import backend.time.handler.KakaoLoginSuccessHandler;
import backend.time.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Autowired
    private final PrincipalDetailService principalDetailService;
    @Autowired
    private final JwtRequestFilter jwtRequestFilter;
    @Autowired
    private final KakaoLoginSuccessHandler kakaoLoginSuccessHandler;
    @Autowired
    private final KakaoLoginFailureHandler kakaoLoginFailureHandler;
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
                .csrf(AbstractHttpConfigurer::disable)
                .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .httpBasic().disable()
                .formLogin().disable()
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        new AntPathRequestMatcher("/kakao/*"),
                                        new AntPathRequestMatcher("/sign-up/*"),//회원가입은 접근 가능
                                        new AntPathRequestMatcher("/login/jwt") //회원가입은 접근 가능
                                ).permitAll()
                                .anyRequest().authenticated() // 회원가입 제외 모두 인증된 사용자만 접근 가능
                )

               .formLogin()

                .successHandler(kakaoLoginSuccessHandler)
                .failureHandler(kakaoLoginFailureHandler);
        return http.build();
    }
}
