package backend.time.controller;


import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.Member;
import backend.time.model.board.Board;
import backend.time.service.ChattingService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("my-page")
public class MyPageController {

    private final ChattingService chattingService;

    /**
     * 채팅 목록 조회 페이지
     */
    @GetMapping("/chat")
    public Result findRoomsByMemberPage(@AuthenticationPrincipal UserDetails userDetails){
        // 1. 채팅 목록 조회 SELECT
        List<ChatRoom> chatList = chattingService.findChatRoomByMember(userDetails.getUsername());

        List<ChatRoomListDto> collect = chatList.stream()
                .map(m -> {ChatRoomListDto dto = new ChatRoomListDto();
                    dto.setName(m.getName());
                    dto.setBoard(m.getBoard());
                    dto.setBuyer(m.getBuyer());
                    dto.setChatMessageList(m.getChatMessageList());

                    return dto;
                }).collect(Collectors.toList());

        return new Result(collect);

    }

    @Data
    static class ChatRoomListDto {
        private String name;
        private Board board;
        private Member buyer;
        private List<ChatMessage> chatMessageList;
    }

    @Data
    @AllArgsConstructor
    static public class Result<T> {
        private T data;
    }
}
