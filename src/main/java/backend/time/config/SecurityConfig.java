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
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .httpBasic().disable()
                .formLogin().disable()
/*                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/sign-up/*"),
                                new AntPathRequestMatcher("/h2-console/**"),
//                                new AntPathRequestMatcher("/kakao/*"),
                                new AntPathRequestMatcher("/oauth/*")
                        ).permitAll()
                        .anyRequest().authenticated())*/
                .authorizeHttpRequests()
//                    .requestMatchers("/","/login","/api/**")
//                    .permitAll()
                .anyRequest()
                .permitAll()
/*                .oauth2Login((oauth2)->oauth2
                        .loginPage("/kakao/login")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(principalDetailService))
                        .successHandler(kakaoLoginSuccessHandler)
                        .failureHandler(kakaoLoginFailureHandler)
                )*/
                .and()
                .formLogin()
                .loginProcessingUrl("/kakao/login")
                .successHandler(kakaoLoginSuccessHandler)
                .failureHandler(kakaoLoginFailureHandler);



//        http
        return http.build();
    }

}
//                .oauth2Login().userInfoEndpoint().userService(principalDetailService)
//                .loginPage("/kakao/login")
//                .authorizationEndpoint().baseUri("/oauth2/authorization")
//                .and()// 소셜 로그인 Url
//                .redirectionEndpoint().baseUri("/oauth/kakao") // 소셜 인증 후 Redirect Url
//                .oauth2Login().loginPage("/kakao/login")
//                 .and()
//                .and()
//                .loginProcessaingUrl("/kakao/login");
//                .successHandler(kakaoLoginSuccessHandler);
//                .failureHandler(kakaoLoginFailureHandler);

/*                .oauth2Login()
                .authorizationEndpoint().baseUri("/kakao/login")
                .and()
                .redirectionEndpoint().baseUri("/oauth/kakao")
                .and()
                .userInfoEndpoint().userService(principalDetailService)*/
/*                .
/*
//

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
//                .userInfoEndpoint().userService(principalDetailService);*/
/*                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
     *//*           .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        new AntPathRequestMatcher("/sign-up/*") //회원가입은 접근 가능
                                ).permitAll()
                                .anyRequest().authenticated() // 회원가입 제외 모두 인증된 사용자만 접근 가능
                )*//*
                .authorizeHttpRequests()
                .anyRequest()
                .permitAll()
                .and()
                .formLogin()
                .loginProcessingUrl("/kakao/login")
                .successHandler(kakaoLoginSuccessHandler)
                .failureHandler(kakaoLoginFailureHandler);
                //X-Frame-Option 안함*/
        //request 인증, 인가 설정
/*                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        new AntPathRequestMatcher("/sign-up/*","/oauth/*") //회원가입은 접근 가능
                                ).permitAll()
                                .anyRequest().authenticated() // 회원가입 제외 모두 인증된 사용자만 접근 가능
                );*/


//logout 안함
