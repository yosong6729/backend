package backend.time.service;

import backend.time.dto.PayResponseDto;
import backend.time.dto.request.PayDto;
import backend.time.model.Member.Member;
import backend.time.model.pay.PayCharge;
import backend.time.repository.MemberRepository;
import backend.time.repository.PayChargeRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
//@RequiredArgsConstructor
public class PayService {

    private final IamportClient iamportClient;
    private final PayChargeRepository payChargeRepository;
    private final MemberRepository memberRepository;

    public PayService(@Value("${REST_API_KEY}") String restApiKey, @Value("${REST_API_SECRET}") String restApiSecret,
                      PayChargeRepository payChargeRepository, MemberRepository memberRepository) {

        this.iamportClient = new IamportClient(restApiKey, restApiSecret);
        this.payChargeRepository = payChargeRepository;
        this.memberRepository = memberRepository;
    }

//    //충전금액 db 저장, member 엔티티 timePay 증가
//    @Transactional
//    public void chargePay(Member member, PayDto payDto) {
//        Member findMember = memberRepository.findById(member.getId())
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 존재하지 않습니다."));
//        findMember.setTimePay(findMember.getTimePay() + payDto.getAmount());
//        PayCharge payCharge = new PayCharge();
//        payCharge.setMember(findMember);
//        payCharge.setAmount(payDto.getAmount());
//        payChargeRepository.save(payCharge);
//    }

    //충전금액 db 저장, member 엔티티 timePay 증가
    @Transactional
    public PayResponseDto verifyPay(Long user_id, String imp_uid) throws IamportResponseException, IOException {
        IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(imp_uid); // 결제 검증 시작
        Long amount = (iamportResponse.getResponse().getAmount()).longValue(); //결제 금액
        String status = iamportResponse.getResponse().getStatus();

        PayResponseDto payResponseDto = PayResponseDto.builder()
                .imp_uid(imp_uid)
                .amount(amount)
                .status(status) //결제 됐으면 "paid"
                .member_id(user_id)
                .build();

        //중복하는 값 없을때 해당 결제가 중복이 아닐때
        if (payChargeRepository.countByImpuidContainsIgnoreCase(imp_uid) == 0) {
            if (iamportResponse.getResponse().getStatus().equals("paid")) {
                Member findMember = memberRepository.findById(user_id)
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 존재하지 않습니다."));
                findMember.setTimePay(findMember.getTimePay() + amount);
                PayCharge payCharge = new PayCharge();
                payCharge.setMember(findMember);
                payCharge.setAmount(amount);
                payCharge.setImpuid(imp_uid);
                payChargeRepository.save(payCharge);
            } else {
                payResponseDto.setStatus("결제 오류입니다.");
            }
        } else {
            payResponseDto.setStatus("이미 결제 되었습니다.");
            return payResponseDto;
        }
        return payResponseDto;
    }

}
