package backend.time.repository;

import backend.time.model.KeywordNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KeywordNotificationRepository extends JpaRepository<KeywordNotification, Long> {

    List<KeywordNotification> findAllByMember_Id(Long id);

    @Override
    void deleteById(Long aLong);
}
