package school.faang.user_service.validator.career;

import school.faang.user_service.dto.career.CareerDateDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.time.LocalDate;
import java.util.Objects;

public class CareerValidator {

    public static void validateCareerDates(CareerDateDto dto) {
        LocalDate from = dto.from();
        LocalDate now = LocalDate.now();

        if (!from.isBefore(now)) {
            throw new DataValidationException("Start date must be in the past - %s".formatted(from));
        }

        if (dto.to() != null && from.isAfter(dto.to())) {
            throw new DataValidationException("Start date cannot be after end date - %s".formatted(from));
        }
    }

    public static void validateOwner(Career career, long userId) {
        if (career.getUser() == null || !Objects.equals(career.getUser().getId(), userId)) {
            throw new ForbiddenException("The user is not the owner of the career");
        }
    }
}