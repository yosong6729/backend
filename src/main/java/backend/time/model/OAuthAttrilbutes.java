package backend.time.model;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthAttrilbutes {
    private String nameAttributeKey;
    private KakaoOauth2UserInfo oauth2UserInfo;
    public static OAuthAttrilbutes ofKako(String userNameAttributeName, Map<String, Object> attributes){
        return OAuthAttrilbutes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOauth2UserInfo(attributes))
                .build();
    }

    public Member toEntity(KakaoOauth2UserInfo kakaoOauth2UserInfo){
        return Member.builder()
                .kakaoId(oauth2UserInfo.getId)
                .role(Member_Role.GUEST)
                .build();
    }


}
