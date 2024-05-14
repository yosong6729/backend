package backend.time.repository;

import backend.time.model.ChatRoom;
import backend.time.model.Member.Member;
import backend.time.model.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>,CustomBoardRepository, JpaSpecificationExecutor<Board> {
    List<Board> findByMember(Member member);
}
