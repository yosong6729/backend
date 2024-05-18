package backend.time.dto;

import backend.time.model.ActivityType;
import lombok.Data;

@Data
public class ActivityNotificationListDto {

    private Long activityId;

    private ActivityType activityType;

    //스크랩 부분
    private String title;

    private String nickName;

    //거래완료 부분
    private String traderName;

    //공통 시간부분
    private String time;

}
