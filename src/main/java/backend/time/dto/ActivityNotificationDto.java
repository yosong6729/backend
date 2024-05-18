package backend.time.dto;

import backend.time.model.ActivityNotification;
import backend.time.model.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class ActivityNotificationDto {

    List<ActivityNotificationListDto> activityNotificationListDtoList;

}
