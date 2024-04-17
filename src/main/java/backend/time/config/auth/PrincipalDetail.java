package backend.time.config.auth;

import backend.time.model.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class PrincipalDetail implements UserDetails {
    private Member member;

    public PrincipalDetail(Member member){this.member=member;}

    //계정이 갖고있는 권한 목록 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority("ROLE_USER"));
        collection.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        return collection;
    }

    @Override
    public String getPassword() {
        //비밀번호와 username을 kakaoId로 함 -> 사용자 보안 문제 발생할 수도 있음(네이버같은 경우 보안을 위해 계속해서 비밀번호를 바꾸라고 하는데 우리는 이러한 보안 과정이 없는 것)
        // 해결방안 , 보안을 위해 refreshToken이나 accessToken의 유효 기간을 줄이기
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(member.getKakaoId());
//        System.out.println("Encoding된 비밀번호" + encodedPassword);
        return encodedPassword;
    }

    @Override // 카카오 id 반환
    public String getUsername() {
        return member.getKakaoId();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



}
