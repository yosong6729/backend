package backend.time.dto;

import backend.time.model.Member.MannerEvaluationCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MannerEvaluationDto {
    @Enumerated(EnumType.STRING)
    private MannerEvaluationCategory mannerEvaluationCategory;

    private Integer mannerEvaluationCount;
}
