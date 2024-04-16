package backend.time.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PointDto {

    private Double longitude;
    private Double latitude;
    private String address;
}