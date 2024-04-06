package backend.time.repository;

import backend.time.model.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BoardRepository extends JpaRepository<Board, Long>,CustomBoardRepository, JpaSpecificationExecutor<Board> {

}
