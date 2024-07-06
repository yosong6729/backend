package backend.time.repository;

import backend.time.model.board.Board;
import backend.time.model.pay.PayStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayStorageRepository extends JpaRepository<PayStorage, Long> {

    Optional<PayStorage> findByBoard(Board board);
}

