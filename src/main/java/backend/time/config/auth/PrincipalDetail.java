package backend.time.config.auth;

import backend.time.model.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
        collection.add(()->{return "ROLE_USER";});
//        collection.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        return collection;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override // 카카오 id 반환
    public String getUsername() {
        return member.getKakaoId();
    }

    public Long getUserId(){return member.getId();}

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
