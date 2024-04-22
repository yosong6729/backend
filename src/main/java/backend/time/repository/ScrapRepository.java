package backend.time.repository;

import backend.time.model.ChatRoom;
import backend.time.model.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap,Long> {
    Optional<Scrap> findByMemberIdAndBoardId(Long memberId, Long boardId);

}
