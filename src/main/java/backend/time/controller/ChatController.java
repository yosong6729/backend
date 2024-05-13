package backend.time.controller;

import backend.time.dto.ChatDto;
import backend.time.model.ChatType;
import backend.time.service.ChattingService;
import backend.time.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

        return chatDto;
    }

}