package backend.time.config.jwt;

import backend.time.config.auth.PrincipalDetail;
import backend.time.exception.KakaoAuthenticationProcessingException;
import com.nimbusds.jwt.JWT;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.interfaces.ECPrivateKey;
import java.util.Date;
import java.util.function.Function;

@Component
@Data
public class JwtTokenUtil {

    @Value("${jwt.secret_key}")
    private String secretKey; // 비밀키

    @Value("${jwt.access.token.expiration.seconds}")
    private Long expirationTime; // 토큰 만료 시간

    @Value("${jwt.refresh.token.expiration.seconds}")
    private Long refreshExpirationTime; // Refresh 토큰 만료 시간

    //RefreshToken 생성
    public String generateRefreshToken(PrincipalDetail principalDetail) {
        return doGenerateToken(principalDetail.getUsername(), refreshExpirationTime,principalDetail.getUserId());
    }

    //AccessToken 생성
    public String generateToken(PrincipalDetail principalDetail) {
        return doGenerateToken(principalDetail.getUsername(), expirationTime,principalDetail.getUserId());
    }

    //JWT 토큰 생성
    private String doGenerateToken(String subject, Long expirationTime, Long userId) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // 토큰 유효성 검사
/*    public boolean validateToken(String token, PrincipalDetail principalDetail) {
        return !isTokenExpired(token) && extractUsername(token).equals(principalDetail.getUsername());
    }*/
    public boolean validateToken(String token) {
       try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e){
            System.out.println("유효하지 않은 토큰입니다."+e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJwt(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
