package backend.time.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    private Long buyerRead;

    private Long sellerRead;

    private Long messageId;

    @OneToMany(mappedBy = "chatMessage")
    private List<ChatImage> chatImages = new ArrayList<>();

    public void addChat(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }


}
