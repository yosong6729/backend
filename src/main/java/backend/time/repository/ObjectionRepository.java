package backend.time.repository;

import backend.time.model.Member.Member;
import backend.time.model.Objection.Objection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjectionRepository extends JpaRepository<Objection,Long> {
    List<Objection> findByObjector(Member member);
}
