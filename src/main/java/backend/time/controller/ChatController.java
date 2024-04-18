package backend.time.controller;

import backend.time.dto.ChatDto;
import backend.time.model.ChatType;
import backend.time.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChattingService chattingService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/chat/enter")
    public void enter(@ModelAttribute ChatDto chatDto){
        chatDto.setMessage(chatDto.getWriter() + "님이 입장하였습니다.");
        chatDto.setType(ChatType.JOIN);
        template.convertAndSend("/sub/chat/room/" + chatDto.getRoomId(), chatDto);
    }

    @MessageMapping("/chat/send")
    public void chat(@ModelAttribute ChatDto chatDto) {
        chattingService.saveChat(chatDto);
        template.convertAndSend("/sub/chat/room/" + chatDto.getRoomId(), chatDto);
    }


}