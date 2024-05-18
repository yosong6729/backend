package backend.time.model;

import backend.time.model.Member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ActivityNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //알림 받은Member

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    //스크랩 부분
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_member")
    private Member scarpMember;

    private String title;

    private String nickName;

    //거래완료 부분
    private String traderName;

    //시간
    @CreationTimestamp
    private Timestamp createDate;

//    @OneToMany(mappedBy = "activityNotification", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ScrapNotification> scrapNotification = new ArrayList<>();
//
//    @OneToMany(mappedBy = "activityNotification", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TransactionNotification> transactionNotification = new ArrayList<>();
}
