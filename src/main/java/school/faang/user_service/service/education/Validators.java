package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.time.Year;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class Validators {

    public static void validateYearFrom(Integer yearFrom) {
        if (yearFrom != null && yearFrom > Year.now().getValue()) {
            log.error("Попытка добавить образование с годом начала в будущем: {}", yearFrom);
            throw new DataValidationException("Год начала обучения не может быть больше текущего");
        }
    }

    public static void validateUserIsEducationOwner(long userId, Education education) {
        User educationOwner = education.getUser();

        if (educationOwner != null && !Objects.equals(userId, educationOwner.getId())) {
            throw new ForbiddenException("Не достаточно прав для получения этих данных");
        }
    }
}
