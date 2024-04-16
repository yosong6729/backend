package backend.time.service;

import backend.time.model.Member;

public interface MemberService {

    Member findMember(String kakaoId);

    Long join(Member member);

    Member findOne(Long memberId);
}
