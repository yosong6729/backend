package backend.time.dto.request;

import backend.time.model.ReportCategory;
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
public class ReportDto {
    @Enumerated(EnumType.STRING)
    private ReportCategory reportCategory; // 신고 사유

}
