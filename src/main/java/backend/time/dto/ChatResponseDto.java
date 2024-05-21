package backend.time.dto;

import backend.time.model.ChatType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ChatResponseDto {
    private Long roomId;
    private Long boardId;
    private Long messageId;
    private String writer;
    private String message;
    private ChatType type;
    private String time;
    private Long buyerRead;
    private Long sellerRead;
    private List<String> images;
}
