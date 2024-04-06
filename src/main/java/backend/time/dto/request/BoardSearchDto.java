package backend.time.dto.request;

import lombok.Data;

//검색할 때의 requestDto
@Data
public class BoardSearchDto {
    private String keyword;
    private int pageNum;
    private String category;
    private Double userLongitude;
    private Double userLatitude;
}