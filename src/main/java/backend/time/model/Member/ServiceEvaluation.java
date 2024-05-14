package backend.time.model.Member;

import backend.time.model.board.BoardCategory;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ServiceEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ServiceEvaluationCategory serviceEvaluationCategory;

    @ManyToOne
    @JoinColumn(name = "member_id") // 데이터베이스에서 참조하는 컬럼 이름을 지정
    private Member member;

    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    private Integer serviceEvaluationCount;
}
