package school.faang.user_service.util;

/**
 * Константы для формирования ответов об ошибках
 */
public final class ErrorResponseConstants {

    private ErrorResponseConstants() {
        // Приватный конструктор для предотвращения создания экземпляров
    }

    // Ключи для ответа об ошибке
    public static final String TIMESTAMP = "timestamp";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String PATH = "path";
    public static final String VALIDATION_ERRORS = "validationErrors";

    // Типы ошибок
    public static final String VALIDATION_ERROR = "Validation Error";
    public static final String FORBIDDEN_ERROR = "Forbidden";
    public static final String NOT_FOUND_ERROR = "Not Found";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    // Сообщения об ошибках
    public static final String INTERNAL_SERVER_ERROR_MESSAGE =
            "Произошла внутренняя ошибка сервера. Пожалуйста, обратитесь к администратору.";
}