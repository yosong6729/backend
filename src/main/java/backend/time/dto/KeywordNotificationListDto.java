package backend.time.dto;

import lombok.Data;

@Data
public class KeywordNotificationListDto {

    private Long keywordId;

    private String title;

    private String keyword;

    private String time;
}
