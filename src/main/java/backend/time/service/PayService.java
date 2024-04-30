package backend.time.service;

import backend.time.dto.request.PayDto;
import backend.time.model.Member;
import backend.time.model.pay.PayCharge;
import backend.time.repository.MemberRepository;
import backend.time.repository.PayChargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PayService {

    final private PayChargeRepository payChargeRepository;
    final private MemberRepository memberRepository;

    //충전금액 db 저장, member 엔티티 timePay 증가
    @Transactional
    public void chargePay(Member member, PayDto payDto) {
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 존재하지 않습니다."));
        findMember.setTimePay(findMember.getTimePay() + payDto.getAmount());
        PayCharge payCharge = new PayCharge();
        payCharge.setMember(findMember);
        payCharge.setAmount(payDto.getAmount());
        payChargeRepository.save(payCharge);
    }
}
