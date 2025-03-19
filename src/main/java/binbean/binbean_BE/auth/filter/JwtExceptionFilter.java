package binbean.binbean_BE.auth.filter;


import binbean.binbean_BE.global.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            // 기존에 작성했던 jwt auth filter 내부 발생하는 jwt exception이라는 예외가 발생했을 때 캐치해서 처리
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");

            var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            String responseJson = mapper.writeValueAsString(errorResponse);
            // json string -> json
            response.getWriter().write(responseJson);
        }
    }
}
