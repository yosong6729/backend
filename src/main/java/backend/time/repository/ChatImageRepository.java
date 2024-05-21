package backend.time.repository;

import backend.time.model.ChatImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatImageRepository extends JpaRepository<ChatImage, Long> {
}

