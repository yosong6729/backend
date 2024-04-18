package backend.time.model;

import backend.time.model.board.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createDate;

    public ChatRoom() {
        this.name = UUID.randomUUID().toString();
    }

    @Builder
    public ChatRoom(Long roomId, String roomName, Board board, Member buyer) {
        this.id = roomId;
        this.name = roomName;
        this.board = board;
        this.buyer = buyer;
    }

    public ChatMessage getLastChat(){
        return getChatMessageList().get(getChatMessageList().size()-1);
    }


//    @Override
//    public String toString() {
//        return "ChatRoom{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", board=" + board +
//                ", buyer=" + buyer +
//                ", chatMessageList=" + chatMessageList +
//                ", createDate=" + createDate +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ChatRoom chatRoom = (ChatRoom) o;
//        return Objects.equals(getId(), chatRoom.getId()) && Objects.equals(getName(), chatRoom.getName()) && Objects.equals(getBoard(), chatRoom.getBoard()) && Objects.equals(getBuyer(), chatRoom.getBuyer()) && Objects.equals(getChatMessageList(), chatRoom.getChatMessageList()) && Objects.equals(getCreateDate(), chatRoom.getCreateDate());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getId(), getName(), getBoard(), getBuyer(), getChatMessageList(), getCreateDate());
//    }
}
