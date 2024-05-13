package backend.time.dto;

import backend.time.model.Member.MannerEvaluationCategory;
import backend.time.model.Member.ServiceEvaluationCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;

public class EvaluationDto {
    public List<MannerEvaluationCategory> mannerEvaluationDtoList;

    public List<ServiceEvaluationCategory> serviceEvaluationDtoList;

}
