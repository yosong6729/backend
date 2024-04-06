package backend.time.controller;

import backend.time.dto.BoardDistanceDto;
import backend.time.dto.ResponseDto;
import backend.time.dto.request.BoardDto;
import backend.time.dto.request.BoardSearchDto;
import backend.time.model.board.Board;
import backend.time.repository.BoardRepository;
import backend.time.service.BoardService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BoardApiController {

    final private BoardService boardService;
    final private BoardRepository boardRepository;

//    //게시글 작성
//    @PostMapping("/api/auth/board")
//    public ResponseDto<String> writeBoard(@ModelAttribute @Valid BoardDto boardDto, @AuthenticationPrincipal PrincipalDetail principalDetail) throws IOException {
//        boardService.write(boardDto, principalDetail.getMember());
//        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
//    }

//    //글 조회(검색)
//    @GetMapping("/api/board")
//    public Result findAll(@ModelAttribute @Valid BoardSearchDto requestDto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
//        Page<Board> boards = boardService.searchBoards(requestDto, principalDetail.getUser());
//
//        // BoardDistanceDto 리스트를 생성
//        List<BoardDistanceDto> boardDistanceDtos = boardRepository.findNearbyOrUnspecifiedLocationBoardsWithDistance(principalDetail.getUser().getUserLongitude(), principalDetail.getUser().getUserLatitude());
//
//        // id를 key로 distance를 값으로 매핑
//        Map<Long, Double> boardIdToDistanceMap = boardDistanceDtos.stream()
//                .collect(Collectors.toMap(BoardDistanceDto::getId, BoardDistanceDto::getDistance));
//
//        // 결과 DTO 리스트를 생성
//        List<BoardListResponseDto> collect = boards.getContent().stream().map(board -> {
//            BoardListResponseDto dto = new BoardListResponseDto();
//            dto.setBoardId(board.getId());
//            dto.setTitle(board.getTitle());
//            dto.setCreatedDate(board.getCreateDate());
//            dto.setChatCount(board.getChatCount());
//            dto.setScrapCount(board.getScrapCount());
//            dto.setDistance(boardIdToDistanceMap.getOrDefault(board.getId(), null));
//
//            return dto;
//        }).collect(Collectors.toList());
//
//        return new Result(collect);
//    }

    @Data
    public class BoardListResponseDto {
        private Long boardId;
        private String title;
        private Timestamp createdDate;
        private int chatCount;
        private int scrapCount;
        private Double distance;

        }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
