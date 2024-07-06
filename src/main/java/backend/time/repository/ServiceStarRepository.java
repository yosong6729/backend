package backend.time.repository;

import backend.time.model.Member.Member;
import backend.time.model.Member.ServiceStar;
import backend.time.model.board.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceStarRepository extends JpaRepository<ServiceStar,Long> {
    Optional<ServiceStar> findByMemberAndBoardCategory(Member member, BoardCategory boardCategory);

}
