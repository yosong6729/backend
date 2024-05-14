package backend.time.controller;

import backend.time.dto.ChatDto;
import backend.time.model.ChatType;
import backend.time.service.ChattingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChattingService chattingService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/chat/enter")
    public void enter(ChatDto chatDto){
        chatDto.setMessage(chatDto.getWriter() + "님이 입장하였습니다.");
        chatDto.setType(ChatType.JOIN);
        template.convertAndSend("/sub/chat/room/" + chatDto.getRoomId(), chatDto);
    }

    @MessageMapping("/chat/send")
    public void chat(ChatDto chatDto) {
        System.out.println(chatDto);
        chattingService.saveChat(chatDto);
        template.convertAndSend("/sub/chat/room/" + chatDto.getRoomId(), chatDto);
    }


}