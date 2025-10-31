package school.faang.user_service.validator.career;

import school.faang.user_service.dto.career.BaseCareerDtoWithDates;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.time.LocalDate;
import java.util.Objects;

public class CareerValidator {

    public static void validateCareerDates(BaseCareerDtoWithDates baseDto) {
        LocalDate from = baseDto.getFrom();
        LocalDate now = LocalDate.now();

        if (!from.isBefore(now)) {
            throw new DataValidationException("Start date must be in the past - %s".formatted(from));
        }

        if (baseDto.getTo() != null && from.isAfter(baseDto.getTo())) {
            throw new DataValidationException("Start date cannot be after end date - %s".formatted(from));
        }
    }

    public static void validateOwner(Career career, long userId) {
        if (career.getUser() == null || !Objects.equals(career.getUser().getId(), userId)) {
            throw new ForbiddenException("The user is not the owner of the career");
        }
    }
}