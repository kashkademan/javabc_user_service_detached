package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.Validator;

@Component
public class EventOwnerValidator implements Validator<EventDto> {
    private static final String ERR_TITLE = "Owner Id must not be null";

    @Override
    public void validate(EventDto obj) {
        if (obj.getOwnerId() == null) {
            throw new DataValidationException(ERR_TITLE);
        }
    }
}