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
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private KakaoLoginSuccessHandler kakaoLoginSuccessHandler;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PrincipalDetailService principleDetailService;

    @Autowired
    private MemberRepository memberRepository;

    private static final String NO_CHECK_URL = "/kakao/login";
    private static final String NO_CHECK_URL2 = "/login";
    private final MemberService memberService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL) ) {
            System.out.println("여긴데?");
            filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
            return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        System.out.println("requestTokenHeader"+requestTokenHeader);

        String kakaoId = null;
        String jwtToken = null;

        if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")){
            jwtToken = requestTokenHeader.substring(7);

            kakaoId = jwtTokenUtil.extractUsername(jwtToken); //return extractClaim(token, Claims::getSubject);
            System.out.println("jwt에서 뽑은 kakaoId"+kakaoId);
        }

        if(SecurityContextHolder.getContext().getAuthentication()==null){

            PrincipalDetail principalDetail1 = (PrincipalDetail) principleDetailService.loadUserByUsername(kakaoId);

            System.out.println("principalDetail"+principalDetail1.getAuthorities());
//                Authentication auth = getAuthentication(memberDto);
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principalDetail, null, principalDetail.getAuthorities());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principalDetail1, null,
                    authoritiesMapper.mapAuthorities(principalDetail1.getAuthorities()));
/*                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));*/

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            System.out.println("Security"+SecurityContextHolder.getContext());

        }
        System.out.println("requeset "+request.getHeader("Authorization")+" response "+response.getStatus());
        filterChain.doFilter(request, response);
    }


}