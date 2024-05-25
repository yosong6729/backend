package backend.time.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ChatRoomResponseDto {

    Long roomId;
    String roleType;
    Long OtherUserId;
    String nickName;
    Long boardId;
    String roomName;
    List<ChatDto> chatlist;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoomResponseDto that = (ChatRoomResponseDto) o;
        return Objects.equals(getRoomId(), that.getRoomId()) && Objects.equals(getRoleType(), that.getRoleType()) && Objects.equals(getOtherUserId(), that.getOtherUserId()) && Objects.equals(getNickName(), that.getNickName()) && Objects.equals(getBoardId(), that.getBoardId()) && Objects.equals(getRoomName(), that.getRoomName()) && Objects.equals(getChatlist(), that.getChatlist());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoomId(), getRoleType(), getOtherUserId(), getNickName(), getBoardId(), getRoomName(), getChatlist());
    }

    @Override
    public String toString() {
        return "ChatRoomResponseDto{" +
                "roomId=" + roomId +
                ", roleType='" + roleType + '\'' +
                ", OtherUserId=" + OtherUserId +
                ", nickName='" + nickName + '\'' +
                ", boardId=" + boardId +
                ", roomName='" + roomName + '\'' +
                ", chatlist=" + chatlist +
                '}';
    }
}
