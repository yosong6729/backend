package backend.time.service;

import backend.time.model.board.Board;

public interface BoardService {

    Board findOne(Long id);
}
