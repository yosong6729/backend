package backend.time.service;

import backend.time.model.board.Board;
import backend.time.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl{

    private final BoardRepository productRepository;

    public Board findOne(Long id) {
        return productRepository.findById(id).get();
    }
}
