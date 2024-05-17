package backend.time.repository;

import backend.time.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Override
    List<Keyword> findAll();

    List<Keyword> findKeywordByMember_Id(Long memberId);


}
