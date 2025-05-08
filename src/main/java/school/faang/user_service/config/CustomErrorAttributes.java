package school.faang.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import school.faang.user_service.util.ErrorResponseConstants;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        // Получаем исключение
        Throwable error = getError(webRequest);

        // Добавляем дополнительную информацию об ошибке
        if (error != null) {
            errorAttributes.put(ErrorResponseConstants.TIMESTAMP, LocalDateTime.now());
            errorAttributes.put("exception", error.getClass().getName());

            // Если это ошибка валидации, добавляем детали
            if (error instanceof MethodArgumentNotValidException validationException) {
                BindingResult bindingResult = validationException.getBindingResult();

                Map<String, String> validationErrors = new HashMap<>();

                // Обработка ошибок валидации полей
                bindingResult.getFieldErrors().forEach(fieldError ->
                        validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

                // Обработка ошибок валидации объектов
                bindingResult.getGlobalErrors().forEach(objectError ->
                        validationErrors.put(objectError.getObjectName(), objectError.getDefaultMessage()));

                // Формирование сообщения об ошибке для логирования
                String errorMessage = validationErrors.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining("; ", "Ошибка валидации данных: ", ""));

                // Логирование ошибки с детальной информацией
                log.error("Validation error: {}", errorMessage);

                errorAttributes.put(ErrorResponseConstants.ERROR, ErrorResponseConstants.VALIDATION_ERROR);
                errorAttributes.put(ErrorResponseConstants.MESSAGE, errorMessage);
                errorAttributes.put(ErrorResponseConstants.VALIDATION_ERRORS, validationErrors);
            } else {
                // Если есть сообщение об ошибке, добавляем его
                if (error.getMessage() != null) {
                    errorAttributes.put(ErrorResponseConstants.MESSAGE, error.getMessage());
                }

                // Логирование ошибки
                log.error("Error occurred: ", error);
            }
        }

        return errorAttributes;
    }
}