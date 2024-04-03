package backend.time.controller;

import backend.time.dto.ResponseDto;
import backend.time.dto.request.BoardDto;
import backend.time.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class BoardApiController {

    final private BoardService boardService;

//    //게시글 작성
//    @PostMapping("/api/auth/board")
//    public ResponseDto<String> writeBoard(@ModelAttribute @Valid BoardDto boardDto, @AuthenticationPrincipal PrincipalDetail principalDetail) throws IOException {
//        boardService.write(boardDto, principalDetail.getMember());
//        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
//    }
}
