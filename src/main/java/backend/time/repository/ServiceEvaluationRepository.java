package backend.time.repository;

import backend.time.model.Member.Member;
import backend.time.model.Member.ServiceEvaluation;
import backend.time.model.board.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceEvaluationRepository extends JpaRepository<ServiceEvaluation,Long> {
    List<ServiceEvaluation> findByMember(Member member);
    List<ServiceEvaluation> findByMemberAndBoardCategory(Member member, BoardCategory boardCategory);
}
