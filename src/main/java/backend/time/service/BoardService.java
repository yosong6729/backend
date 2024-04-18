package backend.time.service;


import backend.time.dto.BoardDistanceDto;
import backend.time.dto.request.BoardDto;
import backend.time.dto.request.BoardSearchDto;
import backend.time.dto.request.PointDto;
import backend.time.model.Member;
import backend.time.model.board.Board;
import backend.time.model.board.BoardCategory;
import backend.time.model.board.BoardType;
import backend.time.repository.BoardRepository;
import backend.time.repository.MemberRepository;
import backend.time.specification.BoardSpecification;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {

    final private BoardRepository boardRepository;
    final private ImageManager imageManager;
    final private MemberRepository memberRepository;
    final private EntityManager entityManager;

    @Transactional
    public void point(PointDto pointDto, Member member) {
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(()->new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
//        Point point = createPoint(pointDto.getLongitude(), pointDto.getLatitude());
//        findMember.setLocation(point);
        findMember.setLongitude(pointDto.getLongitude());
        findMember.setLatitude(pointDto.getLatitude());
        findMember.setAddress(pointDto.getAddress());
        entityManager.flush();
    }

    public Board findOne(Long id) {
        return boardRepository.findById(id).get();
    }

    @Transactional
    public void write(BoardDto boardDto, Member member) throws IOException {
        Board board = new Board();
        board.setCategory(BoardCategory.valueOf(boardDto.getCategory()));
        board.setTitle(boardDto.getTitle());
        board.setItemTime(boardDto.getTime());
        board.setItemPrice(boardDto.getPrice());
        board.setContent(boardDto.getContent());
        board.setBoardType(BoardType.valueOf(boardDto.getBoardType()));

        // 위치 정보가 제공되었는지 확인
        if (boardDto.getLongitude() != null && boardDto.getLatitude() != null) {
            double longitude = boardDto.getLongitude();
            double latitude = boardDto.getLatitude();

            // 위치 정보 설정
            board.setLongitude(longitude);
            board.setLatitude(latitude);
            board.setAddress(boardDto.getAddress());
        }

        board.setMember(member);

        boardRepository.save(board);

        if(boardDto.getImages() !=null) {
            // 이미지 개수 검사
            if (boardDto.getImages().size() > 5) {
                throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
            }

            imageManager.saveImages(boardDto.getImages(), board);
        }

        // Point 객체 생성

//        Point point = createPoint(longitude, latitude);
//        board.setLocation(point); // 위치 설정
    }

    //글 검색 조건 or 페이징
    public Page<Board> searchBoards(BoardSearchDto requestDto, Member member) {
//        Pageable pageable = PageRequest.of(requestDto.getPageNum(), 8);

        // 위치 기반 검색을 위한 ID 리스트 검색
        List<Long> boardIds = null;
//            List<BoardDistanceDto> boardDistanceDtos = boardRepository.findNearbyOrUnspecifiedLocationBoardsWithDistance(member.getLocation().getX(), member.getLocation().getY());
        List<BoardDistanceDto> boardDistanceDtos = boardRepository.findNearbyOrUnspecifiedLocationBoardsWithDistance(member.getLongitude(), member.getLatitude());
            boardIds = boardDistanceDtos.stream()
                    .map(BoardDistanceDto::getId)
                    .collect(Collectors.toList());

//        Specification<Board> spec = Specification.where(null);

//        if (boardIds != null && !boardIds.isEmpty()) {
//            List<Long> finalBoardIds = boardIds;
//            spec = spec.and((root, query, cb) -> root.get("id").in(finalBoardIds));
//        }
        System.out.println(boardIds);

        Specification<Board> spec = Specification.where(BoardSpecification.withIds(boardIds))
                .and(BoardSpecification.withTitleOrContent(requestDto.getKeyword()))
                .and(BoardSpecification.withCategory(requestDto.getCategory()))
                .and(BoardSpecification.withType(requestDto.getBoardType()));

//        // 위치 기반 검색 결과로 필터링
//        if (!boardIds.isEmpty()) {
//            System.out.println("필터링");
//            spec = spec.and(BoardSpecification.withIds(boardIds));
//        }
//
//        // 제목이나 내용으로 검색
//        if (requestDto.getKeyword() != null && !requestDto.getKeyword().isEmpty()) {
//            System.out.println("제목이나 내용으로 검색");
//            spec = spec.and(BoardSpecification.withTitleOrContent(requestDto.getKeyword()));
//        }
//
//        // 카테고리로 검색
//        if (requestDto.getCategory() != null && !requestDto.getCategory().isEmpty()) {
//            spec = spec.and(BoardSpecification.withCategory(requestDto.getCategory()));
//        }
//
//        // 타입으로 검색
//        if (requestDto.getBoardType() != null && !requestDto.getBoardType().isEmpty()) {
//            spec = spec.and(BoardSpecification.withType(requestDto.getBoardType()));
//        }

        String property = "createDate";

        Pageable pageable = PageRequest.of(requestDto.getPageNum(), 8, Sort.by(Sort.Direction.DESC, property));


        return boardRepository.findAll(spec, pageable);
    }

}
