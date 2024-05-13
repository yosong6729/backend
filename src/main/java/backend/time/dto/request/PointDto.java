package backend.time.dto.request;

import lombok.Data;

@Data
public class PointDto {

    private Double longitude;
    private Double latitude;
    private String address;
}