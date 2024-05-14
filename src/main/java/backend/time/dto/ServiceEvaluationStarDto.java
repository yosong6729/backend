package backend.time.dto;

import backend.time.model.board.BoardCategory;
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
public class ServiceEvaluationStarDto {
    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    private Integer starCount; //별 개수

}