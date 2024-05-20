package backend.time.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRoomResponseDto {

    Long roomId;
    String roleType;
    Long userId;
    String nickName;
    Long boardId;
    String roomName;
    List<ChatDto> chatlist;

}
