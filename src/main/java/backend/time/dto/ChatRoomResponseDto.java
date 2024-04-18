package backend.time.dto;

import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ChatRoomResponseDto {

    List<ChatDto> chatlist;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ChatRoomResponseDto that = (ChatRoomResponseDto) o;
//        return Objects.equals(getRoom(), that.getRoom()) && Objects.equals(getChatlist(), that.getChatlist());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getRoom(), getChatlist());
//    }
//
//    @Override
//    public String toString() {
//        return "ChatRoomResponseDto{" +
//                "room=" + room +
//                ", chatlist=" + chatlist +
//                '}';
//    }
}
