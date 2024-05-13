package backend.time.dto;

import backend.time.model.Member_Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private String kakaoId;
    private String nickname;
    private Long mannerTime;
    private Member_Role role;

}

