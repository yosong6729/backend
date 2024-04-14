package backend.time.service;

import backend.time.model.board.Board;
import backend.time.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository productRepository;

    @Override
    public Board findOne(Long id) {
        return productRepository.findById(id).get();
    }
}
