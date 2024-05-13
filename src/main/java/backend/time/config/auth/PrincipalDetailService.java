package backend.time.config.auth;

import backend.time.model.Member.Member;
import backend.time.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String kakaoId) {
        Member member = memberRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->{
                    return null;
//                    return new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : " +kakaoId);
                });
        return new PrincipalDetail(member);
    }
}
