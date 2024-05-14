package backend.time.model.Objection;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ObjectionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uploadFileName; //고객이 업로드한 파일명

    private String storedFileName; //서버 내부에서 관리하는 파일명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="objection_id")
    private Objection objection;

}
