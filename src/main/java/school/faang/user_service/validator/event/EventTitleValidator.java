package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.Validator;

@Component
public class EventTitleValidator implements Validator<EventDto> {
    private static final String ERR_TITLE = "Event title must not be empty";

    @Override
    public void validate(EventDto obj) {
        if (obj.getTitle() == null || obj.getTitle().isBlank()) {
            throw new DataValidationException(ERR_TITLE);
        }
    }
}