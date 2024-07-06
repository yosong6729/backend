package backend.time.dto;

import lombok.Data;

@Data
public class KeywordNotificationListDto {

    private Long boardId;

    private Long keywordId;

    private String title;

    private String keyword;

    private String image;

    private String time;
}
