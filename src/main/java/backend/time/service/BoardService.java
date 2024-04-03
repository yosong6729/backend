package backend.time.service;

import backend.time.dto.request.BoardDto;
import backend.time.model.Member;
import backend.time.model.board.Board;
import backend.time.model.board.BoardCategory;
import backend.time.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

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
        board.setLatitude(boardDto.getLatitude());
        board.setLongitude(boardDto.getLongitude());
        board.setMember(member);

        boardRepository.save(board);
        imageManager.saveImages(boardDto.getImages(), board);
    }
}
