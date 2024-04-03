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
public class Member {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    private Long mannerTime; // 그 사람의 시간(like 당근 온도)

    @CreationTimestamp
    private Timestamp createDate;

    //지도관련
    private String address;
    private Double latitude;
    private Double longitude;
}
