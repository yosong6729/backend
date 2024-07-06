package backend.time.repository;

import backend.time.model.ChatRoom;
import backend.time.model.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {


    @Query("select r from ChatRoom r where r.buyer.kakaoId= :kakaoId or r.board.member.kakaoId= :kakaoId")
    List<ChatRoom> findChatRoomByMember(@Param("kakaoId") String kakaoId);
    Optional<ChatRoom> findByBoardIdAndBuyerId(Long boardId, Long buyerId);

    Optional<ChatRoom> findByName(String name);

    Optional<ChatRoom> findByBoard(Board board);

    @Override
    Optional<ChatRoom> findById(Long roomId);
}
