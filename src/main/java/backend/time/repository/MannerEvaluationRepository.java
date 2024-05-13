package backend.time.repository;

import backend.time.model.Member.MannerEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MannerEvaluationRepository extends JpaRepository<MannerEvaluation,Long> {
}
