package backend.time.service;

import backend.time.model.Member.Member;
import backend.time.model.Scrap;
import backend.time.model.board.Board;
import backend.time.repository.BoardRepository;
import backend.time.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScrapService {
    private final ScrapRepository scrapRepository;
    private final BoardRepository boardRepository;
    //스크랩 & 취소
    @Transactional
    public boolean doScrap(Member member, Long boardId){
        Optional<Scrap> scrap = scrapRepository.findByMemberIdAndBoardId(member.getId(),boardId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(()->new IllegalArgumentException("없는 게시글 입니다."));
        if(scrap.isEmpty()){
            Scrap newScrap = Scrap.builder()
                    .board(board)
                    .member(member)
                    .build();
            scrapRepository.save(newScrap);
            return true;
        }
        else{
            scrapRepository.delete(scrap.get());
            return false;
        }
    }
    // 스크랩 목록 가져오기
    public void getScrapList(Member member){
        scrapRepository.findById(member.getId())
                .orElseThrow(()->new IllegalArgumentException("해당 멤버는 존재하지 않습니다."));

    }
}
