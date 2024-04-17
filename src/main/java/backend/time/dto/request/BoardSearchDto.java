package backend.time.dto.request;

import lombok.Data;

//검색할 때의 requestDto
@Data
public class BoardSearchDto {
    private String keyword;
    private int pageNum = 0;
    private String category;
    private String boardType = "BUY";
}