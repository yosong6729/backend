package backend.time.model.pay;

import backend.time.model.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PayCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member; // 충전을 한 사람

    private Long amount; // 충전 한 금액

    private String impuid; //결제 고유 번호

    @CreationTimestamp
    private Timestamp createDate;
}
