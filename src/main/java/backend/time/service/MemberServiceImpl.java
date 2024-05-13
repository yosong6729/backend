package backend.time.service;

import backend.time.exception.MemberNotFoundException;
import backend.time.model.Member.Member;
import backend.time.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl{

    private final MemberRepository memberRepository;

    public Member findMember(String kakaoId) {
        return memberRepository.findByKakaoId(kakaoId).orElseThrow(()->{throw new MemberNotFoundException();});
    }

    @Transactional
    public Long join(Member member) {
        return memberRepository.save(member).getId();
    }

    public Member findOne(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> {throw new MemberNotFoundException();});
    }
}
