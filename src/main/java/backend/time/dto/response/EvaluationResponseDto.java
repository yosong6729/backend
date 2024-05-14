package backend.time.dto.response;

import backend.time.dto.MannerEvaluationDto;
import backend.time.dto.ServiceEvaluationDto;
import backend.time.dto.ServiceEvaluationStarDto;
import backend.time.model.Member.MannerEvaluation;
import backend.time.model.Member.ServiceEvaluation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResponseDto {
    private List<MannerEvaluationDto> mannerEvaluationList;
    private List<ServiceEvaluationStarDto> serviceEvaluationStarDtoList; //별 개수(평점)
//    private List<ServiceEvaluationDto> serviceEvaluationList;
}
