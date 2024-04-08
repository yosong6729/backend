package backend.time.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

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

    //위도경도를 한번에 위치를 나타내는 점

    private Point location;

//    private Double latitude;
//    private Double longitude;
}
