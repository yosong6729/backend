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
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        System.out.println("requestTokenHeader"+requestTokenHeader);

        String kakaoId = null;
        String kakaoToken = null;

        if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")){
            kakaoToken = requestTokenHeader.substring(7);
//            OAuth2UserRequest oAuth2UserRequest = createOAuth2UserRequest(kakaoToken);
            Map<String, Object> userInfo = memberService.getUserInfo(kakaoToken);
            System.out.println(userInfo);
            kakaoId = userInfo.get("id").toString();
//            System.out.println(oAuth2UserRequest.getClientRegistration() + " and " + oAuth2UserRequest.getAccessToken());
//            PrincipalDetail principalDetail = (PrincipalDetail)principleDetailService.loadUser(oAuth2UserRequest);
//            System.out.println(principalDetail);
//            userName = jwtTokenUtil.extractUsername(jwtToken); //return extractClaim(token, Claims::getSubject);
        }

        if(SecurityContextHolder.getContext().getAuthentication()==null){
//            PrincipalDetail principalDetail = (PrincipalDetail)principleDetailService.loadUser()
/*
            Member member = memberRepository.findByKakaoId(kakaoId)
                    .orElse(null);
            if(member == null){
                MemberDto memberDto = MemberDto.builder()
                        .kakaoId(kakaoId)
                       */
/* .nickname(member.getNickname())
                        .mannerTime(member.getMannerTime())*//*

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
*/

//            PrincipalDetail principalDetail = (PrincipalDetail) principleDetailService.loadUser(kakaoId);
            PrincipalDetail principalDetail1 = (PrincipalDetail) principleDetailService.loadUserByUsername(kakaoId);
            System.out.println("principalDetail"+principalDetail1.getUsername());
//                Authentication auth = getAuthentication(memberDto);
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principalDetail, null, principalDetail.getAuthorities());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principalDetail1, null,
                        authoritiesMapper.mapAuthorities(principalDetail1.getAuthorities()));
/*                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));*/

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            System.out.println("Security"+SecurityContextHolder.getContext());

            if(response.getStatus()==200){
//                System.out.println(usernamePasswordAuthenticationToken.);
                kakaoLoginSuccessHandler.onAuthenticationSuccess(request, response,usernamePasswordAuthenticationToken);
            }


        }
        System.out.println("requeset "+request.getHeader("Authorization")+" response "+response.getStatus());
        filterChain.doFilter(request, response);
    }
    public Authentication getAuthentication(MemberDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    } //수정해야함

}