package backend.time.service;

import backend.time.model.Member;

public interface MemberService {

    Member findMember(String email);

    Long join(Member member);

    Member findOne(Long memberId);
}
