package backend.time.model.Member;

import backend.time.model.board.BoardCategory;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ServiceStar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalScore =0;

    private Integer totalCount = 0;

    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


}
