package backend.time.dto;

import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.ChatType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Slf4j
public class ChatDto {
    private Long roomId;
    private Long boardId;
    private Long messageId;
    private String writer;
    private String message;
    private ChatType type;
    private String time;
    private Long buyerRead;
    private Long sellerRead;
    private List<String> image;
    private List<MultipartFile> images;


    public ChatMessage toEntity(ChatRoom chatRoom) {
        ChatMessage chat = ChatMessage.builder()
                .type(this.type)
                .writer(this.writer)
                .message(this.message)
                .buyerRead(this.buyerRead)
                .sellerRead(this.sellerRead)
                .messageId(this.messageId)
                .build();
        chat.addChat(chatRoom);
        return chat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatDto chatDto = (ChatDto) o;
        return Objects.equals(getRoomId(), chatDto.getRoomId()) && Objects.equals(getBoardId(), chatDto.getBoardId()) && Objects.equals(getMessageId(), chatDto.getMessageId()) && Objects.equals(getWriter(), chatDto.getWriter()) && Objects.equals(getMessage(), chatDto.getMessage()) && getType() == chatDto.getType() && Objects.equals(getTime(), chatDto.getTime()) && Objects.equals(getBuyerRead(), chatDto.getBuyerRead()) && Objects.equals(getSellerRead(), chatDto.getSellerRead()) && Objects.equals(getImages(), chatDto.getImages());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoomId(), getBoardId(), getMessageId(), getWriter(), getMessage(), getType(), getTime(), getBuyerRead(), getSellerRead(), getImages());
    }

    @Override
    public String toString() {
        return "ChatDto{" +
                "roomId=" + roomId +
                ", boardId=" + boardId +
                ", messageId=" + messageId +
                ", writer='" + writer + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", time='" + time + '\'' +
                ", buyerRead=" + buyerRead +
                ", sellerRead=" + sellerRead +
                ", images=" + images +
                '}';
    }
}
