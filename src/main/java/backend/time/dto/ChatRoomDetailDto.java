package backend.time.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDetailDto {
    private String name; //구매자 이름
    private String message; //마지막 채팅
    private String time; //마지막 채팅시간 ex)몇분전
    //확인안한 채팅수 추가해야함
}