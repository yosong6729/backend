package backend.time.repository;

import backend.time.model.board.Board;
import backend.time.model.board.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findByBoard(Board board);
}
