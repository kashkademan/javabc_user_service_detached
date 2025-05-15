package school.faang.user_service.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.validation.EndDateValidatable;
import school.faang.user_service.validation.ValidEndDate;

import java.time.LocalDateTime;

@Slf4j
public class EndDateValidator implements ConstraintValidator<ValidEndDate, EndDateValidatable> {
    private String message;

    @Override
    public void initialize(ValidEndDate constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(EndDateValidatable value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDateTime startDate = value.getStartDate();
        LocalDateTime endDate = value.getEndDate();

        if (endDate == null) {
            return true;
        }

        if (startDate == null) {
            return false;
        }

        boolean isValid = endDate.isAfter(startDate);

        if (!isValid) {
            // Логируем ошибку валидации
            log.error("Validation error: endDate {} is not after startDate {}", endDate, startDate);

            // Отключаем стандартное сообщение об ошибке
            context.disableDefaultConstraintViolation();

            // Добавляем кастомное сообщение об ошибке
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }

        return isValid;
    }
}
