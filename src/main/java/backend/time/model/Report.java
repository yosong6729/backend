package backend.time.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportCategory reportCategory;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createDate;

    private Long reportedBoardId; // 신고 당한 게시글

    private Long reporterId; //신고 한 사람

    private Long reportedId; //신고 당한 사람


}
