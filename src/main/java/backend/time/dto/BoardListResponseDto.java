package backend.time.dto;

import backend.time.model.board.BoardState;
import backend.time.model.board.BoardType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BoardListResponseDto {
    private Long boardId;
    private String title;
    private String itemTime;
    private Long itemPrice;
    private Timestamp createdDate;
    private int chatCount;
    private int scrapCount;
    private Double distance;
    private String address;
    private BoardState boardState;
    private String firstImage;
    private BoardType boardType;
}
