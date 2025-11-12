package school.faang.user_service.config.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorResponse;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        if (isSwaggerUri(path)) {
            chain.doFilter(request, response);
            return;
        }

        String userIdHeader = req.getHeader("x-user-id");
        if (userIdHeader == null) {
            sendErrorResponse(res, req, HttpStatus.FORBIDDEN,
                    "'x-user-id' header is missing", ForbiddenException.class);
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            sendErrorResponse(res, req, HttpStatus.BAD_REQUEST,
                    "Invalid user ID format", IllegalArgumentException.class);
            return;
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            sendErrorResponse(res, req, HttpStatus.NOT_FOUND,
                    String.format("User %s not found", userId), EntityNotFoundException.class);
            return;
        }

        userContext.setUser(user.get());
        userContext.setUserId(userId);

        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }

    private <T extends RuntimeException> void sendErrorResponse(HttpServletResponse res,
                                                                HttpServletRequest req,
                                                                HttpStatus status,
                                                                String message,
                                                                Class<T> errorType) throws IOException {
        res.setStatus(status.value());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                req.getRequestURL().toString(),
                errorType.getSimpleName(),
                message,
                status.value()
        );

        res.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private boolean isSwaggerUri(String path) {
        return path.contains("swagger") || path.contains("api-docs");
    }
}
