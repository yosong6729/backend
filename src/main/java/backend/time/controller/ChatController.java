package backend.time.controller;

import backend.time.dto.ChatDto;
import backend.time.service.ChattingService;
import backend.time.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChattingService chattingService;
    private final MemberServiceImpl memberService;

    @PostMapping("/chat/send")
    public ChatDto send(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ChatDto chatDto) {
        //프론트에서 roomId, message, type만 보내면됨
        String kakaoId = userDetails.getUsername();
        String nickname = memberService.findMember(kakaoId).getNickname();
        chatDto.setWriter(nickname);
        chattingService.saveChat(chatDto);


        int hour = LocalDateTime.now().getHour();
        int minute = LocalDateTime.now().getMinute();
        String time = hour + ":" + minute;
        chatDto.setTime(time);
        return chatDto;
    }

}