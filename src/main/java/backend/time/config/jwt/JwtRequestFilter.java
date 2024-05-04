package backend.time.config.jwt;

import backend.time.config.auth.PrincipalDetail;
import backend.time.config.auth.PrincipalDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PrincipalDetailService principleDetailService;

//    private static final String NO_CHECK_URL = "/login/jwt";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        System.out.println("doFilter");
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 헤더에서 토큰 추출
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwt = requestTokenHeader.substring(7);

            username =jwtTokenUtil.extractUsername(jwt);
        }

        // 추출된 username을 사용하여 인증
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            PrincipalDetail principalDetail = (PrincipalDetail)principleDetailService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwt)) {
                System.out.println("유효한 토큰"+principalDetail.getUsername()+", "+principalDetail.getPassword());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principalDetail, null, principalDetail.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
/*        else{ // 토큰이 유효하지 않을 때
//            exceptionCall(response, "invalidToken");
            filterChain.doFilter(request, response);
            return;
        }*/


        filterChain.doFilter(request, response);
    }
}