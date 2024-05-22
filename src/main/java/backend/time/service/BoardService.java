package backend.time.service;

import backend.time.dto.BoardDistanceDto;
import backend.time.dto.request.*;
import backend.time.model.ChatRoom;
import backend.time.model.Member.Member;
import backend.time.model.pay.Account;
import backend.time.model.pay.PayMethod;
import backend.time.model.pay.PayStorage;
import backend.time.repository.*;
import backend.time.model.board.Board;
import backend.time.model.board.BoardCategory;
import backend.time.model.board.BoardType;
import backend.time.model.board.Image;
import backend.time.repository.BoardRepository;
import backend.time.repository.ImageRepository;
import backend.time.repository.MemberRepository;
import backend.time.specification.BoardSpecification;
import jakarta.persistence.EntityManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static backend.time.model.board.BoardState.*;
import static backend.time.model.pay.PayMethod.*;
import static backend.time.model.board.BoardType.SELL;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {

    final private BoardRepository boardRepository;
    final private ImageManager imageManager;
    final private MemberRepository memberRepository;
    final private EntityManager entityManager;
    final private ImageRepository imageRepository;
    final private ChatRoomRepository chatRoomRepository;
    final private PayStorageRepository payStorageRepository;
    final private AccountRepository accountRepository;

//    @Transactional
//    public void point(PointDto pointDto, Member member) {
//        Member findMember = memberRepository.findById(member.getId())
//                .orElseThrow(()->new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
////        Point point = createPoint(pointDto.getLongitude(), pointDto.getLatitude());
////        findMember.setLocation(point);
//        findMember.setLongitude(pointDto.getLongitude());
//        findMember.setLatitude(pointDto.getLatitude());
//        findMember.setAddress(pointDto.getAddress());
//        entityManager.flush();
//    }

    @Transactional
    public void point(PointDto pointDto) {
        Member findMember = memberRepository.findByKakaoId(pointDto.getKakaoId())
                .orElseThrow(()->new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        findMember.setLongitude(pointDto.getLongitude());
        findMember.setLatitude(pointDto.getLatitude());
        findMember.setAddress(pointDto.getAddress());
        entityManager.flush();
    }

    public Board findOne(Long id) {
        return boardRepository.findById(id).get();
    }

    @Transactional
    public Long write(BoardDto boardDto, Member member) throws IOException {
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

        Board savedBoard = boardRepository.save(board);
        Long boardId = savedBoard.getId();
        if(boardDto.getImages() !=null) {
            // 이미지 개수 검사
            if (boardDto.getImages().size() > 5) {
                throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
            }

            List<Image> images = imageManager.saveImages(boardDto.getImages(), board);
            
            for (Image image : images) {
                board.addImage(image);
            }
        }

        return boardId;

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

    @Transactional
    public void update(Long id, BoardUpdateDto boardUpdateDto) throws IOException {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        if(board.getBoardState()==RESERVED || board.getBoardState()==SOLD){
            throw new IllegalArgumentException("거래 중이거나 판매 완료된 글은 수정할 수 없습니다.");
        }
        board.setTitle(boardUpdateDto.getTitle());
        board.setContent(boardUpdateDto.getContent());
        board.setItemPrice(boardUpdateDto.getPrice());
        board.setItemTime(boardUpdateDto.getTime());
        board.setAddress(boardUpdateDto.getAddress());
        board.setLongitude(boardUpdateDto.getLongitude());
        board.setLatitude(boardUpdateDto.getLatitude());
        board.setCategory(BoardCategory.valueOf(boardUpdateDto.getCategory()));
        board.setBoardType(BoardType.valueOf(boardUpdateDto.getBoardType()));
        List<MultipartFile> images = boardUpdateDto.getImages();
        //사진 받고 있던 거면 냅두고 없으면 추가 없어진 건 삭제
        if (!images.isEmpty()) {
            List<Image> findImages = imageRepository.findByBoard(board);
            Set<String> imageNames = images.stream()
                    .map(MultipartFile::getOriginalFilename)
                    .collect(Collectors.toSet());

            findImages.removeIf(findImage -> {
                boolean toDelete = !imageNames.contains(findImage.getStoredFileName());
                if (toDelete) {
                    board.removeImage(findImage);
                    imageRepository.delete(findImage); // 이미지 삭제
                }
                return toDelete;
            });
            List<MultipartFile> newImages = new ArrayList<>();
            // 새로운 이미지 추가
            for (MultipartFile image : images) {
                if (findImages.stream().noneMatch(findImage -> Objects.equals(findImage.getStoredFileName(), image.getOriginalFilename()))) {
                    newImages.add(image);
                }
            }
            List<Image> imageList = imageManager.saveImages(newImages, board);

            for (Image image : imageList) {
                board.addImage(image);
            }
        }
    }

    @Transactional
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public void payMeth(PayMethDto paymethdto, Long boardId, Long chatId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채팅방이 존재하지 않습니다."));
        //거래중인 글 or 거래완료글이면 예외처리
        if(board.getBoardState()==RESERVED || board.getBoardState()==SOLD){
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        PayMethod payMethod = PayMethod.valueOf(paymethdto.getPayMeth());
        //거래방식 저장
        board.setPayMethod(payMethod);
        if (payMethod.equals(PAY)) {  //틈새페이를 선택했을때
                // 틈새페이 지불하는 사람이 board 가격만큼 현재 있는지 확인 없으면 -> 예외터지게 -> 프론트가 예외처리
                //있으면 차감하는 로직
            if(board.getBoardType().equals(SELL)){
                //판매글일때.. 채팅하기 누른사람(buyer)이 돈을 지불
                if(board.getItemPrice() > chatRoom.getBuyer().getTimePay()) {
                    throw new IllegalArgumentException("틈새페이를 충전해주세요");
                }
                else {
                    chatRoom.getBuyer().setTimePay(chatRoom.getBuyer().getTimePay() - board.getItemPrice());
                    //포인트가 임시저장소로 이동
                    PayStorage storage = PayStorage.builder()
                            .member(chatRoom.getBuyer())
                            .amount(board.getItemPrice())
                            .board(board)
                            .build();
                    payStorageRepository.save(storage);
                }
            }else {
                //구매글일때.. 채팅하기 누른사람(buyer)말고 글쓴사람 writer가 돈을 지불
                if(board.getItemPrice() > board.getMember().getTimePay()) {
                    throw new IllegalArgumentException("틈새페이를 충전해주세요");
                }
                else {
                    board.getMember().setTimePay(board.getMember().getTimePay() - board.getItemPrice());
                    //포인트가 임시저장소로 이동
                    PayStorage storage = PayStorage.builder()
                            .member(board.getMember())
                            .amount(board.getItemPrice())
                            .board(board)
                            .build();
                    payStorageRepository.save(storage);
                }
            }
        } else if(payMethod.equals(ACCOUNT)) {
            Account account = Account.builder()
                    .accountNumber(paymethdto.getAccountNumber())
                    .bank(paymethdto.getBank())
                    .member(member)
                    .board(board)
                    .chatRoom(chatRoom)
                    .holder(paymethdto.getHolder()).build();
            accountRepository.save(account);
        }
        board.setBoardState(RESERVED);
    }

    @Transactional
    public void cancel(Long boardId, Long chatId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채팅방이 존재하지 않습니다."));
        //거래중인 글 아니면 예외처리
        if(board.getBoardState()==SALE || board.getBoardState()==SOLD){
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        //틈새페이는 환불해줘야함
        if(board.getPayMethod().equals(PAY)) {
            PayStorage storage = payStorageRepository.findByBoard(board)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 저장소가 존재하지 않습니다."));

            if (board.getBoardType().equals(SELL)) {
                //판매글일때 채팅하기 누른사람(buyer)이 돈을 지불한 사람
                chatRoom.getBuyer().setTimePay(chatRoom.getBuyer().getTimePay() + storage.getAmount());
            } else {
                //구매글일때 채팅하기 누른사람(buyer)말고 글쓴사람 writer가 돈을 지불한 사람
                board.getMember().setTimePay(board.getMember().getTimePay() + storage.getAmount());
            }
            //스토리지 삭제
            payStorageRepository.delete(storage);
        }
        board.setBoardState(SALE);
    }

//    @Transactional
//    public void saveAccount(AccountDto accountdto, Long boardId, Long chatId, Long userId) {
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 게시물이 존재하지 않습니다."));
//        Member member = memberRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 존재하지 않습니다."));
//        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 채팅방이 존재하지 않습니다."));
//        Account account = Account.builder()
//                .accountNumber(accountdto.getAccountNumber())
//                .bank(accountdto.getBank())
//                .member(member)
//                .board(board)
//                .chatRoom(chatRoom)
//                .holder(accountdto.getHolder()).build();
//        accountRepository.save(account);
//    }

    @Transactional
    public void complete(Long boardId, Long chatId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채팅방이 존재하지 않습니다."));
        //틈새페이는 상대방에게 이동
        if(board.getPayMethod().equals(PAY)) {
            PayStorage storage = payStorageRepository.findByBoard(board)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 저장소가 존재하지 않습니다."));

            if (board.getBoardType().equals(SELL)) {
                //판매글일때 채팅하기 누른사람(buyer)이 돈을 지불한 사람
                board.getMember().setTimePay(board.getMember().getTimePay() + storage.getAmount());
            } else {
                //구매글일때 채팅하기 누른사람(buyer)말고 글쓴사람 writer가 돈을 지불한 사람
                chatRoom.getBuyer().setTimePay(chatRoom.getBuyer().getTimePay() + storage.getAmount());
            }
            //스토리지 삭제
            payStorageRepository.delete(storage);
            board.setTrader(chatRoom.getBuyer());
        }
        board.setBoardState(SOLD);
    }

//    public AccountResponseDto getAccount(Long boardId, Long chatId) {
//        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 채팅방이 존재하지 않습니다."));
//        Account account = accountRepository.findByChatRoom(chatRoom)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 계좌 정보가 존재하지 않습니다."));
//        AccountResponseDto accountResponseDto = new AccountResponseDto();
//        accountResponseDto.setAccountNumber(account.getAccountNumber());
//        accountResponseDto.setBank(account.getBank());
//        accountResponseDto.setHolder(account.getHolder());
//        return accountResponseDto;
//    }

    //로그인한사람 (나) seller 인지 buyer인지 알려줌
    public WhoResponseDto buyWho(Long boardId, Long chatId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채팅방이 존재하지 않습니다."));
        WhoResponseDto whoResponseDto = new WhoResponseDto();
        if (board.getBoardType().equals(SELL)) {
            //판매글일때 채팅하기 누른사람(buyer)이 돈을 지불한 사람
            if(Objects.equals(member.getId(), chatRoom.getBuyer().getId())){
                whoResponseDto.setRole("buyer");
            } else{
                whoResponseDto.setRole("seller");
            }
        } else {
            //구매글일때 채팅하기 누른사람(buyer)말고 글쓴사람 writer가 돈을 지불한 사람
            if(Objects.equals(member.getId(), board.getMember().getId())) {
                whoResponseDto.setRole("buyer");
            } else{
                whoResponseDto.setRole("seller");
            }
        }
        return whoResponseDto;
    }

    //작성한 내역(판매글, 구매글) - userId = memberId
    public List<Board> writeList(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 존재하지 않습니다."));
        List<Board> boards = boardRepository.findByMemberOrderByCreateDateDesc(member);
        return boards;
    }

    //거래한 내역 - userId = traderId
    public List<Board> tradeList(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 존재하지 않습니다."));
        List<Board> boards = boardRepository.findByTraderOrderByCreateDateDesc(member);
        return boards;
    }

    @Data
    public class AccountResponseDto {
        private String holder; // 예금주
        private String bank; // 은행
        private Long accountNumber; //계좌번호
    }

    @Data
    public class WhoResponseDto {
        private String role;
    }

}
