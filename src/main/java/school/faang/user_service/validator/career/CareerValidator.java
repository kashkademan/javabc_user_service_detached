package school.faang.user_service.validator.career;

import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDate;

public class CareerValidator {

    public static void checkCareerDates(CreateCareerDto createCareerDto) {
        LocalDate from = createCareerDto.from();
        if (createCareerDto.to() != null && createCareerDto.from().isAfter(createCareerDto.to())) {
            throw new DataValidationException("Start date cannot be after end date - %s".formatted(from));
        }
    }
}