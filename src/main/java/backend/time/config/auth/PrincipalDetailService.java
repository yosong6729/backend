package backend.time.config.auth;

import backend.time.model.CustomOAuth2User;
import backend.time.model.Member;
import backend.time.model.Member_Role;
import backend.time.model.OAuthAttrilbutes;
import backend.time.repository.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
//public class PrincipalDetailService implements UserDetailsService {
public class PrincipalDetailService implements UserDetailsService {
//public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String kakaoId) {
        Optional<Member> member = memberRepository.findByKakaoId(kakaoId);
        if(member.isEmpty()){
            Member newMem = Member.builder()
                    .kakaoId(kakaoId)
                    .role(Member_Role.GUEST)
                    .build();
            return new PrincipalDetail(newMem);
        }

        return new PrincipalDetail(member.get());
    }

}
/*
    @Override // 카카오한테 회원 프로필 받는 함수
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("PrincipalDetail.loadUser()실행 - OAuth2 로그인 요청 진입");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        //	2번
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        System.out.println("oAuth2User" + oAuth2User);
        //	3번
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); //kakao
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); //id

        System.out.println("registrationId = {}" + registrationId); //kakao
        System.out.println("userNameAttributeName = {}" + userNameAttributeName); //id
//        Long kakaoId = (Long) oAuth2User.getAttributes().get(userNameAttributeName);

        Map<String, Object> memberAttribute = oAuth2User.getAttributes();

        OAuthAttrilbutes extractAttributes = OAuthAttrilbutes.ofKako(userNameAttributeName, memberAttribute);  //id, id
        Member createdUser = getMember(extractAttributes);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority((String) createdUser.getRole().getKey())),
                memberAttribute,
                extractAttributes.getNameAttributeKey(),
                createdUser.getRole()
        );
    }*/
/*    public PrincipalDetail loadUserByKakao(String kakaoId)throws UsernameNotFoundException {
        Member principal = memberRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : " + kakaoId);
                });
        return new PrincipalDetail(principal);
    }*/

   /* private Member getMember(OAuthAttrilbutes attrilbutes) {
        Member member = memberRepository.findByKakaoId(attrilbutes.getOauth2UserInfo().getId)
                .orElse(null);
        if (member == null) {
            return saveMember(attrilbutes);
        }
        return member;

    }

    private Member saveMember(OAuthAttrilbutes attrilbutes) {
        Member member = attrilbutes.toEntity(attrilbutes.getOauth2UserInfo());
        return memberRepository.save(member);
    }*/

/*
    @Override
    public OAuth2User loadUser(String kakaoId)throws UsernameNotFoundException {
        Member principal = memberRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : " + kakaoId);
                });
        return new PrincipalDetail(principal);
    }
*/