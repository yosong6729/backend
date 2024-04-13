package backend.time.exception;

import org.springframework.security.core.AuthenticationException;

public class KakaoAuthenticationProcessingException extends AuthenticationException {
    public KakaoAuthenticationProcessingException(String msg, Throwable t){
        super(msg,t);
    }
    public KakaoAuthenticationProcessingException(String msg){
        super(msg);
    }
}
