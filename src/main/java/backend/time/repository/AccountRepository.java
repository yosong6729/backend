package backend.time.repository;

import backend.time.model.ChatRoom;
import backend.time.model.pay.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByChatRoom(ChatRoom chatRoom);
}
