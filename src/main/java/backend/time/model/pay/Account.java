package backend.time.model.pay;

import backend.time.model.ChatRoom;
import backend.time.model.Member.Member;
import backend.time.model.board.Board;
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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member; // 계좌 보낸 사람

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom; // 계좌 보낸 채팅방

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board; // 계좌 보낸 채팅방

    private String holder; // 예금주
    private String bank; // 은행
    private Long accountNumber; //계좌번호

    @CreationTimestamp
    private Timestamp createDate;
}
