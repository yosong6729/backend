package backend.time.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRoomResponseDto {

    Long roomId;
    String roleType;
    List<ChatDto> chatlist;

}
