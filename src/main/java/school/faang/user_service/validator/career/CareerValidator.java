package school.faang.user_service.validator.career;

import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.time.LocalDate;
import java.util.Objects;

public class CareerValidator {

    public static void checkCareerDates(CreateCareerDto createCareerDto) {
        LocalDate from = createCareerDto.from();
        if (createCareerDto.to() != null && createCareerDto.from().isAfter(createCareerDto.to())) {
            throw new DataValidationException("Start date cannot be after end date - %s".formatted(from));
        }
    }

    public static void validateOwner(Career career, User user) {
        if (!Objects.equals(career.getUser(), user)) {
            throw new ForbiddenException("The user is not the owner of the career");
        }
    }

    public static void validateDates(LocalDate localDateFrom, LocalDate localDateTo) {
        if (localDateTo != null && localDateFrom != null &&
                localDateFrom.isAfter(localDateTo)) {
            throw new DataValidationException("Start date cannot be after end date - %s".formatted(localDateFrom));
        }
    }
}