package backend.time.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //설정 파일임 명시
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOriginPatterns("*") //외부에서 들어오는 모든 url 허용
                .allowedMethods("*") //모든 HTTP메소드(GET,POST등)의 요청 허용
                .allowCredentials(true); // 자격증명 허용
    }

    private final String CORS_HEADER = "*";

    @Value("${file.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + uploadDir);
    }


}
