package backend.time.controller;

import backend.time.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();


    //프론트 쪽에서 사용자가 '로그인'을 하면, 해당 사용자를 sseEmitters 에 등록
    @GetMapping(value = "/api/notification/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        String kakaoId = userDetails.getUsername();
        SseEmitter sseEmitter = notificationService.subscribe(kakaoId);
        return sseEmitter;
    }

}
