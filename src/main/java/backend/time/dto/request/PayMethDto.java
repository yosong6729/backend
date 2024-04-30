package backend.time.dto.request;

import lombok.Data;

@Data
public class PayMethDto {
    private String payMeth;
    //계좌이체일때
    private String holder; // 예금주
    private String bank; // 은행
    private Long accountNumber; //계좌번호
}
