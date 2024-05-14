package backend.time.config.jwt;

import backend.time.config.auth.PrincipalDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
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
        return doGenerateToken(principalDetail.getUsername(), refreshExpirationTime);
    }

    //AccessToken 생성
    public String generateToken(PrincipalDetail principalDetail) {
        return doGenerateToken(principalDetail.getUsername(), expirationTime);
    }

    //JWT 토큰 생성
    private String doGenerateToken(String subject, long expirationMilliseconds) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationMilliseconds);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }


    // 유효성 & 탈취 및 위변조 확인
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

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        System.out.println("claims는"+claims);
        if(claims == null){
            return null;
        }
        else{
            return claimsResolver.apply(claims);
        }
        //caims "iss" : 토큰 발행자, "sub" 토큰 대상자, "aud" 토큰 수신자, "exp" 토큰 만료 시간 "nbf" 토큰 유효하기 시작한 시간 "iat" 토큰 발행된 시간 "jti" 토큰 식별자
        // 우리는 sub, iat, exp 가 있음
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (Exception e){
            System.out.println("유효하지 않은 토큰입니다."+e.getMessage());
            return null;
        }
    }


}
