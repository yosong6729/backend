package backend.time.controller;

import backend.time.config.auth.PrincipalDetail;
import backend.time.dto.BoardDistanceDto;
import backend.time.dto.BoardListResponseDto;
import backend.time.dto.ResponseDto;
import backend.time.dto.request.ScrapDto;
import backend.time.model.Scrap;
import backend.time.model.board.Board;
import backend.time.service.ScrapService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class ScrapApiController {
    private final ScrapService scrapService;

    // 스크랩 하기 & 스크랩 취소
    @PostMapping("api/board/{id}/scrap")
    public ResponseDto scrap(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable("id") Long id){
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
    public Result scrapList(@ModelAttribute @Valid ScrapDto scrapDto, @AuthenticationPrincipal PrincipalDetail principalDetail){
        Page<Scrap> scraps = scrapService.getScrapList(scrapDto, principalDetail.getMember());

        List<Board> boards = scraps.stream()
                .map(Scrap::getBoard)
                .collect(Collectors.toList());

        List<BoardDistanceDto> boardDistanceDtos = boards.stream().map(board -> {
            if(board.getAddress() !=null) {
                double distance = calculateDistanceInKm(principalDetail.getMember().getLatitude(), principalDetail.getMember().getLongitude(), board.getLatitude(), board.getLongitude());
                return new BoardDistanceDto(board.getId(), Math.round(distance * 10) / 10.0);
            }
            else{
                return new BoardDistanceDto(board.getId(),0D);
            }
        }).collect(Collectors.toList());

        // id를 key로 distance를 값으로 매핑
        Map<Long, Double> boardIdToDistanceMap = boardDistanceDtos.stream()
                .collect(Collectors.toMap(BoardDistanceDto::getId, BoardDistanceDto::getDistance));

        // 결과 DTO 리스트를 생성
        List<BoardListResponseDto> collect = boards.stream().map(board -> {
            BoardListResponseDto dto = new BoardListResponseDto();
            dto.setBoardId(board.getId());
            dto.setTitle(board.getTitle());
            dto.setCreatedDate(board.getCreateDate());
            dto.setChatCount(board.getChatCount());
            dto.setScrapCount(board.getScrapCount());
            dto.setBoardState(board.getBoardState());
            dto.setDistance(boardIdToDistanceMap.get(board.getId()));
            if(board.getAddress() !=null) {
                dto.setAddress(board.getAddress());
            }
            //이미지가 있으면 첫번째 사진의 storedFileName 넘겨줌 없으면 null
            if (!board.getImages().isEmpty()) {
                dto.setFirstImage(board.getImages().get(0).getStoredFileName());
            }

            return dto;
        }).collect(Collectors.toList());

        return new Result(collect);
    }

    double calculateDistanceInKm(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.01;
        // 위도, 경도를 라디안으로 변환
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine 공식
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.pow(Math.sin(dLon / 2), 2)
                * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
