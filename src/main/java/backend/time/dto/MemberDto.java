package backend.time.dto;

import backend.time.model.Member;
import backend.time.model.Member_Role;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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

