package backend.time.service;

import backend.time.dto.BoardDistanceDto;
import backend.time.dto.request.BoardDto;
import backend.time.dto.request.BoardSearchDto;
import backend.time.model.Member;
import backend.time.model.board.Board;
import backend.time.model.board.BoardCategory;
import backend.time.repository.BoardRepository;
import backend.time.specification.BoardSpecification;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {

    final private BoardRepository boardRepository;
    final private ImageManager imageManager;

    @Transactional
    public void write(BoardDto boardDto, Member member) throws IOException {
        // 이미지 개수 검사
        if (boardDto.getImages().size() > 5) {
            throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
        }
        Board board = new Board();
        board.setBoardCategory(BoardCategory.valueOf(boardDto.getCategory()));
        board.setTitle(boardDto.getTitle());
        board.setItemTime(boardDto.getTime());
        board.setItemPrice(boardDto.getPrice());
        board.setContent(boardDto.getContent());
        board.setAddress(boardDto.getAddress());

        double longitude = boardDto.getLongitude();
        double latitude = boardDto.getLatitude();
        // Point 객체 생성
        Point point = new GeometryFactory().createPoint(new Coordinate(longitude, latitude));
        point.setSRID(4326); // WGS84 좌표계 설정
        board.setLocation(point); // 위치 설정
        // board 객체를 저장하는 로직 (예: repository.save(board))
//        board.setLatitude(boardDto.getLatitude());
//        board.setLocation(boardDto.getLongitude());
        board.setMember(member);

        boardRepository.save(board);
        imageManager.saveImages(boardDto.getImages(), board);
    }

//    //글 검색 조건 or 페이징
//    public Page<Board> searchBoards(BoardSearchDto requestDto, Member member) {
//        Pageable pageable = PageRequest.of(requestDto.getPageNum(), 8);
//
//        // 위치 기반 검색을 위한 ID 리스트 검색
//        List<Long> boardIds = null;
//        if (requestDto.getUserLongitude() != null && requestDto.getUserLatitude() != null) {
//            List<BoardDistanceDto> boardDistanceDtos = boardRepository.findNearbyOrUnspecifiedLocationBoardsWithDistance(member.getUserLongitude(), member.getUserLatitude());
//            boardIds = boardDistanceDtos.stream()
//                    .map(BoardDistanceDto::getId)
//                    .collect(Collectors.toList());
//        }
//
//        Specification<Board> spec = Specification.where(null);
//
//        if (boardIds != null && !boardIds.isEmpty()) {
//            List<Long> finalBoardIds = boardIds;
//            spec = spec.and((root, query, cb) -> root.get("id").in(finalBoardIds));
//        }
//
//        // 제목이나 내용으로 검색
//        if (requestDto.getKeyword() != null && !requestDto.getKeyword().isEmpty()) {
//            spec = spec.and(BoardSpecification.withTitleOrContent(requestDto.getKeyword()));
//        }
//
//        // 카테고리로 검색
//        if (requestDto.getCategory() != null && !requestDto.getCategory().isEmpty()) {
//            spec = spec.and(BoardSpecification.withCategory(requestDto.getCategory()));
//        }
//
//        return boardRepository.findAll(spec, pageable);
//    }
}
