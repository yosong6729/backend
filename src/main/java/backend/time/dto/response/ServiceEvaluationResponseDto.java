package backend.time.dto.response;

import backend.time.dto.ServiceEvaluationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEvaluationResponseDto {
        private List<ServiceEvaluationDto> serviceEvaluationList;

}
