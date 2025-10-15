package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.time.Year;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class Validators {

    public static void validateYearFromYearTo(Integer yearFrom, Integer yearTo) {
        if (yearFrom != null && yearFrom > Year.now().getValue()) {
            log.error("Попытка добавить образование с годом начала в будущем: {}", yearFrom);
            throw new DataValidationException("Год начала обучения не может быть больше текущего");
        }

        if (yearTo != null && yearFrom != null && yearTo < yearFrom) {
            log.error("Попытка добавить образование с годом окончания {} большем чем начало обучения: {}", yearTo,
                    yearFrom);
            throw new DataValidationException("Год окончание обучения не может быть меньше года начала обучения");
        }
    }

    public static void validateUserIsEducationOwner(long userId, Education education) {
        User educationOwner = education.getUser();

        if (educationOwner == null) {
            throw new ForbiddenException("Образование не может быть без пользователя");
        }

        if (!Objects.equals(userId, educationOwner.getId())) {
            throw new ForbiddenException("Не достаточно прав для получения этих данных");
        }
    }
}
