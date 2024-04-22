package backend.time.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BoardUpdateDto {

    private String title;
    private String content;

    //글에 담겨있는 주소 정보 (주소, 경도, 위도)
    private String address;
    private Double longitude;
    private Double latitude;

    //board 가테고리, state, type
    private String boardState;
    private String category;
    private String boardType;

    //이미지들
    List<MultipartFile> images;
}
