package backend.time.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writer;

    private String message;

    @Enumerated(EnumType.STRING)
    private ChatType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createDate;


    public void addChat(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ChatMessage that = (ChatMessage) o;
//        return Objects.equals(getId(), that.getId()) && Objects.equals(getWriter(), that.getWriter()) && Objects.equals(getMessage(), that.getMessage()) && getType() == that.getType() && Objects.equals(getChatRoom(), that.getChatRoom()) && Objects.equals(getCreateDate(), that.getCreateDate());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getId(), getWriter(), getMessage(), getType(), getChatRoom(), getCreateDate());
//    }
//
//    @Override
//    public String toString() {
//        return "ChatMessage{" +
//                "id=" + id +
//                ", writer='" + writer + '\'' +
//                ", message='" + message + '\'' +
//                ", type=" + type +
//                ", chatRoom=" + chatRoom +
//                ", createDate=" + createDate +
//                '}';
//    }
}
