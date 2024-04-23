package backend.time.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardDistanceDto {
    private Long id;
    private Double distance;
}

