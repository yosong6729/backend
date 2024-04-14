package backend.time.config.jwt;

import backend.time.config.auth.PrincipalDetail;
import backend.time.config.auth.PrincipalDetailService;
import backend.time.dto.MemberDto;
import backend.time.handler.KakaoLoginSuccessHandler;
import backend.time.model.Member;
import backend.time.model.Member_Role;
import backend.time.repository.MemberRepository;
import backend.time.service.MemberService;
import com.nimbusds.openid.connect.sdk.federation.registration.ClientRegistrationType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PrincipalDetailService principleDetailService;

    private static final String NO_CHECK_URL = "/kakao/login";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 헤더에서 토큰 추출
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwt = requestTokenHeader.substring(7);
            username =jwtTokenUtil.extractUsername(jwt);
        }
 /*       else{
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }
            username = requestBody.toString();
            username = username.replace("\"", "");
        }*/
        // 추출된 username을 사용하여 인증
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            PrincipalDetail principalDetail = (PrincipalDetail)principleDetailService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwt)) {
                System.out.println("유효한 토큰"+principalDetail.getUsername()+", "+principalDetail.getPassword());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principalDetail, null, principalDetail.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        filterChain.doFilter(request, response);
/*
        String kakaoId = null;
        String kakaoToken = null;

        if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")){
            kakaoToken = requestTokenHeader.substring(7);
//            OAuth2UserRequest oAuth2UserRequest = createOAuth2UserRequest(kakaoToken);
//            Map<String, Object> userInfo = memberService.getUserInfo(kakaoToken);
//            System.out.println(userInfo);
//            kakaoId = userInfo.get("id").toString();
//            System.out.println(oAuth2UserRequest.getClientRegistration() + " and " + oAuth2UserRequest.getAccessToken());
//            PrincipalDetail principalDetail = (PrincipalDetail)principleDetailService.loadUser(oAuth2UserRequest);
//            System.out.println(principalDetail);
//            userName = jwtTokenUtil.extractUsername(jwtToken); //return extractClaim(token, Claims::getSubject);
        }

        if(SecurityContextHolder.getContext().getAuthentication()==null){
//            PrincipalDetail principalDetail = (PrincipalDetail)principleDetailService.loadUser()
*/
/*
            Member member = memberRepository.findByKakaoId(kakaoId)
                    .orElse(null);
            if(member == null){
                MemberDto memberDto = MemberDto.builder()
                        .kakaoId(kakaoId)
                       *//*

         */
/* .nickname(member.getNickname())
                        .mannerTime(member.getMannerTime())*//*
         */
/*

                        .role(member.getRole())
                        .build();
            }
            else{
                MemberDto memberDto = MemberDto.builder()
                        .kakaoId(member.getKakaoId())
                        .nickname(member.getNickname())
                        .mannerTime(member.getMannerTime())
                        .role(member.getRole())
                        .build();
            }
*//*


//            PrincipalDetail principalDetail = (PrincipalDetail) principleDetailService.loadUser(kakaoId);
            PrincipalDetail principalDetail1 = (PrincipalDetail) principleDetailService.loadUserByUsername(kakaoId);

            System.out.println("principalDetail"+principalDetail1.getAuthorities());
//                Authentication auth = getAuthentication(memberDto);
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principalDetail, null, principalDetail.getAuthorities());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principalDetail1, null,
                    authoritiesMapper.mapAuthorities(principalDetail1.getAuthorities()));
*/
/*                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));*//*


            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            System.out.println("Security"+SecurityContextHolder.getContext());

//            if(response.getStatus()==200){
//                System.out.println(usernamePasswordAuthenticationToken.);
//                kakaoLoginSuccessHandler.onAuthenticationSuccess(request, response,usernamePasswordAuthenticationToken);
//            }


        }
        System.out.println("requeset "+request.getHeader("Authorization")+" response "+response.getStatus());
        filterChain.doFilter(request, response);
    }
    public Authentication getAuthentication(MemberDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    } //수정해야함
*/}
}