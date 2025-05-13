package school.faang.user_service.handler;

@FunctionalInterface
public interface ErrorHandler {
    String handle(Exception ex);
}
