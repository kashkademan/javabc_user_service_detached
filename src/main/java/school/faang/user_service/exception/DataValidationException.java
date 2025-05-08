package school.faang.user_service.exception;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class DataValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public DataValidationException(String message) {
        super(message);
        this.errors = null;
    }

    public DataValidationException(Map<String, String> errors) {
        super(formatErrorMessage(errors));
        this.errors = errors;
    }

    private static String formatErrorMessage(Map<String, String> errors) {
        if (errors == null || errors.isEmpty()) {
            return "Ошибка валидации данных";
        }

        return errors.entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; ", "Ошибка валидации данных: ", ""));
    }
}
