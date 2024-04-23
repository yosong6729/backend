package backend.time.dto;

import backend.time.model.board.BoardState;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BoardListResponseDto {
    private Long boardId;
    private String title;
    private Timestamp createdDate;
    private int chatCount;
    private int scrapCount;
    private Double distance;
    private String address;
    private BoardState boardState;
    private String firstImage;
}
