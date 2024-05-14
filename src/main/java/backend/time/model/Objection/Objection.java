package backend.time.model.Objection;

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
public class Objection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ObjectionStatus status; // 신고 진행 상태

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objector_member_id")
    private Member objector; //이의제기 한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objected_member_id")
    private Member objected; //이의제기 당한 사람

    @OneToMany(mappedBy = "objection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObjectionImage> images = new ArrayList<>();

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createDate;

}
