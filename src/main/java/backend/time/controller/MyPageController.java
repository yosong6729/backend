package backend.time.controller;


import backend.time.dto.ChatRoomDetailDto;
import backend.time.model.ChatRoom;
import backend.time.service.ChattingService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
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
        List<ChatRoom> chatRoomList = chattingService.findChatRoomByMember(userDetails.getUsername());

        List<ChatRoomDetailDto> collect = chatRoomList.stream()
                .map(m -> {ChatRoomDetailDto dto = new ChatRoomDetailDto();
                    dto.setRoomId(m.getId());
                    dto.setRoomName(m.getName());
                    dto.setName(m.getLastChatWriter());   //마지막 채팅 작성자 이름
                    dto.setMessage(m.getLastChat().getMessage());  //마지막 채팅
                    dto.setTime(Time.calculateTime(m.getLastChat().getCreateDate())); //마지막 채팅시간 ex)몇분전

                    return dto;
                }).collect(Collectors.toList());


        ChatRoomListDto chatRoomListDto = new ChatRoomListDto();
        chatRoomListDto.setChatRoomDetails(collect);

        return new Result<>(chatRoomListDto);
    }

    @Data
    static class ChatRoomListDto {
        private List<ChatRoomDetailDto> chatRoomDetails;

    }



    @Data
    @AllArgsConstructor
    static public class Result<T> {
        private T data;
    }




    public static class Time {
        private static class TIME_MAXIMUM {
            public static final int SEC = 60;
            public static final int MIN = 60;
            public static final int HOUR = 24;
            public static final int DAY = 30;
            public static final int MONTH = 12;
        }
        public static String calculateTime(Date date) {
            long curTime = System.currentTimeMillis();
            long regTime = date.getTime();
            long diffTime = (curTime - regTime) / 1000;
            String msg = null;
            if (diffTime < TIME_MAXIMUM.SEC) {
                // sec
                msg = diffTime + "초 전";
            } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
                // min
                msg = diffTime + "분 전";
            } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
                // hour
                msg = (diffTime) + "시간 전";
            } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
                // day
                msg = (diffTime) + "일 전";
            } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
                // day
                msg = (diffTime) + "달 전";
            } else {
                msg = (diffTime) + "년 전";
            }
            return msg;
        }
    }
}
