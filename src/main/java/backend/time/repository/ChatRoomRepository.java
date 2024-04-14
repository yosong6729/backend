package backend.time.repository;

import backend.time.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {


    @Query("select r from ChatRoom r where r.buyer.email= :email or r.board.member.email= :email")
    List<ChatRoom> findChatRoomByMember(@Param("email") String email);
    Optional<ChatRoom> findByBoardIdAndBuyerId(Long boardId, Long buyerId);

    Optional<ChatRoom> findByName(String name);
}
