package school.faang.user_service.exception.handler;

public record Violation(
        String fieldName,
        String message
) {
}
