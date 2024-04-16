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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kakaoId; //카카오에서 주는 회원 고유 번호, 2018년 이후에 출시된 앱은 해당 회원이 탈퇴를 해도 같은 회원 번호를 부여 받음

    @Column(unique = true, length = 10)
    private String nickname; // 닉네임 10자 제한

    @Enumerated(EnumType.STRING)
    private Member_Role role;

    @Builder.Default
    private Long mannerTime = 15L; // 그 사람의 시간(like 당근 온도) //기본 15분

    @CreationTimestamp
    private Timestamp createDate;

    //지도관련
    private String address;
    private Double latitude;
    private Double longitude;
}
