package backend.time.model;


import jakarta.persistence.Entity;
import lombok.*;

import java.util.Map;
@Getter
public class KakaoOauth2UserInfo  {
    public Map<String, Object> attributes;
    public  String getId;

    public KakaoOauth2UserInfo(Map<String, Object> attributes){
        this.attributes = attributes;
    }
}
