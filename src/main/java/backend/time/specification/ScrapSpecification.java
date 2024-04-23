package backend.time.specification;

import backend.time.model.Member;
import backend.time.model.Scrap;
import org.springframework.data.jpa.domain.Specification;

public class ScrapSpecification {

    //멤버로 찾기
    public static Specification<Scrap> withMember(Member member) {
        return (root, query, cb) -> cb.equal(root.get("member"), member);
    }

}