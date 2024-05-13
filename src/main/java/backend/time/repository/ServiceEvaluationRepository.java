package backend.time.repository;

import backend.time.model.Member.ServiceEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceEvaluationRepository extends JpaRepository<ServiceEvaluation,Long> {
}
