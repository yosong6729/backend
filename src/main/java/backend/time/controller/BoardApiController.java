package backend.time.controller;

import backend.time.config.auth.PrincipalDetail;
import backend.time.dto.BoardDistanceDto;
import backend.time.dto.BoardListResponseDto;
import backend.time.dto.ResponseDto;
import backend.time.dto.request.*;
import backend.time.model.Scrap;
import backend.time.model.board.*;
import backend.time.repository.BoardRepository;
import backend.time.repository.ChatRoomRepository;
import backend.time.repository.ScrapRepository;
import backend.time.service.BoardService;
import backend.time.service.ChattingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardApiController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final ScrapRepository scrapRepository;
    private final ChattingService chattingService;
    private final ChatRoomRepository chatRoomRepository;

    //user 위치 넣기
    @PostMapping("/api/auth/point")
    public ResponseDto<String> addPoint(@RequestBody @Valid PointDto pointDto) throws IOException {
        boardService.point(pointDto);
        return new ResponseDto<String>(HttpStatus.OK.value(),"위치 설정 성공");
    }

    //게시글 작성
    @PostMapping("/api/auth/board")
    public ResponseDto<String> writeBoard(@ModelAttribute @Valid BoardDto boardDto, @AuthenticationPrincipal PrincipalDetail principalDetail) throws IOException {
        boardService.write(boardDto, principalDetail.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
    }

    //글 조회(검색)
    @GetMapping("/api/board")
    public Result findAll(@ModelAttribute @Valid BoardSearchDto requestDto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        Page<Board> boards = boardService.searchBoards(requestDto, principalDetail.getMember());

        // BoardDistanceDto 리스트를 생성
        List<BoardDistanceDto> boardDistanceDtos = boardRepository.findNearbyOrUnspecifiedLocationBoardsWithDistance(principalDetail.getMember().getLongitude(), principalDetail.getMember().getLatitude());
        // id를 key로 distance를 값으로 매핑
        Map<Long, Double> boardIdToDistanceMap = boardDistanceDtos.stream()
                .collect(Collectors.toMap(BoardDistanceDto::getId, BoardDistanceDto::getDistance));

        UserAddressResponseDto userAddressResponseDto = new UserAddressResponseDto();
        userAddressResponseDto.setUserLongitude(principalDetail.getMember().getLongitude());
        userAddressResponseDto.setUserLatitude(principalDetail.getMember().getLatitude());
        userAddressResponseDto.setAddress(principalDetail.getMember().getAddress());
        // 결과 DTO 리스트를 생성
        List<BoardListResponseDto> collect = boards.getContent().stream().map(board -> {
            BoardListResponseDto dto = new BoardListResponseDto();
            dto.setBoardId(board.getId());
            dto.setTitle(board.getTitle());
            dto.setItemPrice(board.getItemPrice());
            dto.setItemTime(board.getItemTime());
            dto.setCreatedDate(board.getCreateDate());
            dto.setChatCount(board.getChatCount());
            dto.setScrapCount(board.getScrapCount());
            dto.setBoardState(board.getBoardState());
            dto.setDistance(boardIdToDistanceMap.getOrDefault(board.getId(), null));
            if(board.getAddress() !=null) {
            dto.setAddress(board.getAddress());
            }
            //이미지가 있으면 첫번째 사진의 storedFileName 넘겨줌 없으면 null
            if (!board.getImages().isEmpty()) {
                dto.setFirstImage(board.getImages().get(0).getStoredFileName());
            }

            return dto;
        }).collect(Collectors.toList());

        BoardResponseWrapper responseWrapper = new BoardResponseWrapper();
        responseWrapper.setUserAddress(userAddressResponseDto);
        responseWrapper.setBoards(collect);

        return new Result<>(responseWrapper);
    }

    //글 상세보기
    @GetMapping("/api/board/{id}")
    public Result boardDetail(@PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        Board board = boardRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 글이 존재하지 않습니다."));
        BoardDetailResponseDto boardDetailResponseDto = new BoardDetailResponseDto();
        boardDetailResponseDto.setBoardId(board.getId());
        Optional<Scrap> scrap = scrapRepository.findByMemberIdAndBoardId(principalDetail.getMember().getId(), id);
        if(scrap.isEmpty()){
            boardDetailResponseDto.setScrapStus("NO");
        }
        else {
            boardDetailResponseDto.setScrapStus("YES");
        }

        if(Objects.equals(board.getMember().getId(), principalDetail.getMember().getId())) {
            boardDetailResponseDto.setWho("writer");
        } else {
            boardDetailResponseDto.setWho("reader");
//            Optional<ChatRoom> chatRoom = chatRoomRepository.findByBoard(board);
//            if(chatRoom.isPresent()){
//            boardDetailResponseDto.setRoomName(chatRoom.get().getName());
//                String roomName = chattingService.findChatRoomByBuyer(boardId, member.getId()).getName();
//                log.info("roomName = {}", roomName);
//            }
            String roomName = chattingService.findChatRoomByBuyer(board.getId(), principalDetail.getMember().getId()).getName();
            log.info("roomName = {}", roomName);
            boardDetailResponseDto.setRoomName(roomName);
        }
        boardDetailResponseDto.setUserId(board.getMember().getId());
        boardDetailResponseDto.setNickname(board.getMember().getNickname());
        boardDetailResponseDto.setMannerTime(board.getMember().getMannerTime());
        boardDetailResponseDto.setTitle(board.getTitle());
        boardDetailResponseDto.setContent(board.getContent());
        boardDetailResponseDto.setItemPrice(board.getItemPrice());
        boardDetailResponseDto.setItemTime(board.getItemTime());
        boardDetailResponseDto.setCreatedDate(board.getCreateDate());
        boardDetailResponseDto.setChatCount(board.getChatCount());
        boardDetailResponseDto.setScrapCount(board.getScrapCount());
        boardDetailResponseDto.setAddress(board.getAddress());
        boardDetailResponseDto.setLongitude(board.getLongitude());
        boardDetailResponseDto.setLatitude(board.getLatitude());
        boardDetailResponseDto.setBoardState(board.getBoardState());
        boardDetailResponseDto.setCategory(board.getCategory());
        boardDetailResponseDto.setBoardType(board.getBoardType());
        List<Image> images = board.getImages();
        List<String> collect = images.stream().map(Image::getStoredFileName)
                .toList();
        boardDetailResponseDto.setImages(collect);

        return new Result<>(boardDetailResponseDto);
    }

    //게시글 수정
    @PutMapping("/api/auth/board/{id}")
    public ResponseDto<String> updateBoard(@PathVariable("id") Long id, @ModelAttribute @Valid BoardUpdateDto boardUpdateDto, @AuthenticationPrincipal PrincipalDetail principalDetail) throws IOException {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        if(!Objects.equals(principalDetail.getMember().getId(), board.getMember().getId())){
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        boardService.update(id, boardUpdateDto);
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 수정 완료");
    }

    //게시글 삭제
    @DeleteMapping("/api/auth/board/{id}")
    public ResponseDto<String> deleteBoard(@PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetail principalDetail) throws IOException {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        if(!Objects.equals(principalDetail.getMember().getId(), board.getMember().getId())){
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        boardService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 삭제 완료");
    }

    //<------------------채팅 버튼 별 board 상태 변경-------------------->
    //결제 방법 선택
    @PostMapping("api/board/{boardId}/chat/{chatId}/pay")
    public ResponseDto payMeth(@RequestBody PayMethDto paymethdto, @PathVariable("boardId") Long boardId, @PathVariable("chatId") Long chatId, @AuthenticationPrincipal PrincipalDetail principalDetail) throws IOException {
        boardService.payMeth(paymethdto,boardId,chatId, principalDetail.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(),"거래중으로 변경 됨");
    }

    //거래 취소
    @PutMapping("api/board/{boardId}/chat/{chatId}/cancel")
    public ResponseDto cancel(@PathVariable("boardId") Long boardId, @PathVariable("chatId") Long chatId) throws IOException {
        boardService.cancel(boardId, chatId);
        //틈새페이는 다시 환불해주는 로직 작성해야함
        return new ResponseDto<String>(HttpStatus.OK.value(),"판매중으로 변경 됨");
    }

    //거래 완료 틈새페이 상대방에게 이동
    @PutMapping("api/board/{boardId}/chat/{chatId}/complete")
    public ResponseDto complete(@PathVariable("boardId") Long boardId, @PathVariable("chatId") Long chatId) throws IOException {
        boardService.complete(boardId, chatId);
        //틈새페이는 다시 환불해주는 로직 작성해야함
        return new ResponseDto<String>(HttpStatus.OK.value(),"판매완료로 변경 됨");
    }

//    //계좌 저장
//    @PostMapping("/api/board/{boardId}/chat/{chatId}/account")
//    public ResponseDto saveAccount(@RequestBody AccountDto accountdto, @PathVariable("boardId") Long boardId, @PathVariable("chatId") Long chatId, @AuthenticationPrincipal PrincipalDetail principalDetail) {
//        boardService.saveAccount(accountdto, boardId, chatId, principalDetail.getMember().getId());
//            return new ResponseDto(HttpStatus.OK.value(), "계좌 저장 완료");
//    }

    @Data
    public class BoardDetailResponseDto{
        //roomName 채팅방 있으면 채팅방이름 넘겨주고 없으면 null
        private String roomName;
        //본인이 쓴 글인지 확인
        private String who; //reader, writer
        //boardId 게시글 식별자
        private Long boardId;
        private String scrapStus;
        //글쓴 사람 닉네임, 틈새시간
        private Long userId;
        private String nickname;
        private Long mannerTime;
        //글의 기본 정보 (제목,내용,글쓴날짜)
        private String title;
        private String content;
        private Timestamp createdDate;
        private String itemTime;
        private Long itemPrice;
        //채팅수, 스크랩수
        private int chatCount;
        private int scrapCount;
        //글에 담겨있는 주소 정보 (주소, 경도, 위도)
        private String address;
        private Double longitude;
        private Double latitude;
        //board 가테고리, state, type
        private BoardState boardState;
        private BoardCategory category;
        private BoardType boardType;
        //이미지들
        private List<String> images;
    }

    @Data
    public class UserAddressResponseDto{
        private Double userLongitude;
        private Double userLatitude;
        private String address;
    }

    @Data
    public class BoardResponseWrapper {
        private UserAddressResponseDto userAddress;
        private List<BoardListResponseDto> boards;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
