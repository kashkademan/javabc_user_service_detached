package school.faang.user_service.validation.career;

import school.faang.user_service.entity.Career;
import school.faang.user_service.exceptions.DataValidationException;

import java.time.LocalDate;

public class CareerValidation {
    public static void validateDateFrom(Career career) {
        if (!career.getDateFrom().isBefore(LocalDate.now())) {
            throw new DataValidationException("Дата начала карьеры не может быть больше текущей даты.");
        }
    }
}