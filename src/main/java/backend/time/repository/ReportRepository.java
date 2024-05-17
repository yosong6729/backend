package backend.time.repository;

import backend.time.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {
    Report findByReporterIdAndReportedBoardId(Long memberId, Long boardId);

}
