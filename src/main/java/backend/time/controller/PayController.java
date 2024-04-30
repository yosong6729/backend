package backend.time.controller;

import backend.time.config.auth.PrincipalDetail;
import backend.time.dto.ResponseDto;
import backend.time.dto.request.PayDto;
import backend.time.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PayController {

    final private PayService payService;

    @PostMapping("api/charge/point")
    public ResponseDto charge(@RequestBody PayDto payDto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        payService.chargePay(principalDetail.getMember(), payDto);
        return new ResponseDto<String>(HttpStatus.OK.value(),"틈새페이 충전 완료");
    }
}
