package backend.time.controller;

import backend.time.config.auth.PrincipalDetail;
import backend.time.dto.ResponseDto;
import backend.time.service.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ScrapApiController {
    private final ScrapService scrapService;

    // 스크랩 하기 & 스크랩 취소
    @PostMapping("api/board/{id}/scrap")
    public ResponseDto scrap(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long id){
        Boolean isScrap = scrapService.doScrap(principalDetail.getMember(), id);
        Map<String, Object> data = new HashMap<>();
        if(isScrap){
            data.put("isScrap",true);
            return new ResponseDto(HttpStatus.OK.value(),data);
        }
        else{
            data.put("isScrap",false);
            return new ResponseDto(HttpStatus.OK.value(),data);
        }
    }

    // 스크랩 목록 가져오기
    @GetMapping("api/scrap-list")
    public void scrapList(@AuthenticationPrincipal PrincipalDetail principalDetail){
        scrapService.getScrapList(principalDetail.getMember());
    }
}
