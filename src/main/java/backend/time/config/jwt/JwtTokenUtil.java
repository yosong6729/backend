package backend.time.config.jwt;

import backend.time.config.auth.PrincipalDetail;
import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
