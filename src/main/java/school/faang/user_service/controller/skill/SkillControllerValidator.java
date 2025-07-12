package school.faang.user_service.controller.skill;

import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;

@Controller
public class SkillControllerValidator {

    public void validationParameters(Object object) {
        if (object == null) {
            throw new DataValidationException("'%s' не может быть null".formatted(object));
        }

        if (!(object instanceof Number)) {
            throw new DataValidationException("'%s' должен быть числом".formatted(object));
        }

        long id = ((Number) object).longValue();
        if (id == 0) {
            throw new DataValidationException("'%s' не может быть 0".formatted(object));
        }
    }
}
