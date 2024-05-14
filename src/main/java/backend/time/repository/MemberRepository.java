package backend.time.repository;

import backend.time.model.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByKakaoId(String kakaoId);
}
