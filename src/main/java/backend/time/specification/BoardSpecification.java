package backend.time.specification;

import backend.time.model.board.Board;
import backend.time.model.board.BoardCategory;
import org.springframework.data.jpa.domain.Specification;

public class BoardSpecification {

    // 제목이나 내용으로 검색
    public static Specification<Board> withTitleOrContent(String keyword) {
        return (root, query, cb) -> cb.or(
                cb.like(root.get("title"), "%" + keyword + "%"),
                cb.like(root.get("content"), "%" + keyword + "%")
        );
    }

    // 카테고리로 검색
    public static Specification<Board> withCategory(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), BoardCategory.valueOf(category));
    }

}
