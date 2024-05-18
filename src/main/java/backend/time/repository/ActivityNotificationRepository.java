package backend.time.repository;

import backend.time.model.ActivityNotification;
import backend.time.model.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityNotificationRepository extends JpaRepository<ActivityNotification, Long> {

    Optional<ActivityNotification> findActivityNotificationByMember(Member member);

    List<ActivityNotification> findActivityNotificationByMember_IdOrderByCreateDate(Long id);

    ActivityNotification findActivityNotificationById(Long id);

}
