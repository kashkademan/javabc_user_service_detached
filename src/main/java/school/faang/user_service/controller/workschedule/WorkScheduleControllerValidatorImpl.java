package school.faang.user_service.controller.workschedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.exception.ForbiddenException;

import java.util.Objects;

@Slf4j
@Component
public class WorkScheduleControllerValidatorImpl implements WorkScheduleControllerValidator {

    @Override
    public void validateUserAccess(long userId, WorkScheduleDto workScheduleDto) {
        if (!Objects.equals(workScheduleDto.id(), userId)) {
            log.error("user_id ({}) doesn't match workScheduleDto user_id ({})", workScheduleDto.id(), userId);
            throw new ForbiddenException("User does not belong to this work schedule.");
        }
    }
}
