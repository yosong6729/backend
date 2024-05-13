package backend.time.dto;

import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
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
