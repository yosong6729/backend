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
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    private Integer serviceEvaluationCount; //각 서비스 평가당 받은 개수

/*    private Integer evaluationTotalCount; //각 서비스당 받은 개수

    private Integer serviceEvaluationScore = 0; // 각 서비스 평가의 점수(평균을 구하기 위한)

    private Integer serviceEvaluationAVG = 0; // 평균*/
}
