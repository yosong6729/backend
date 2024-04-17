package backend.time.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BoardDto {

    private String category;

    private String boardType;

    @NotEmpty
    @Length(max = 20)
    private String title;

    @NotEmpty
    private String time;

    //나눔일때는 null로
    private Long price;

    @NotEmpty
    @Length(max = 500)
    private String content;

    //지도
    private String address;

    private Double latitude;

    private Double longitude;


    List<MultipartFile> images;
}
