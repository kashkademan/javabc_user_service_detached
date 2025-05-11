package school.faang.user_service.dto.event.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.validation.EndDateValidatable;
import school.faang.user_service.validation.ValidEndDate;

import java.time.LocalDateTime;
import java.util.List;

import static school.faang.user_service.util.LogsConstants.NOT_POSITIVE_NUMBER;
import static school.faang.user_service.util.LogsConstants.NULL_ID;
import static school.faang.user_service.util.LogsConstants.NULL_START_DATE;
import static school.faang.user_service.util.LogsConstants.NULL_TITLE;
import static school.faang.user_service.util.LogsConstants.TEXT_LIMIT_FROM_3_TO_255;
import static school.faang.user_service.util.LogsConstants.TEXT_LIMIT_TO_2000;
import static school.faang.user_service.util.LogsConstants.WRONG_END_DATE;
import static school.faang.user_service.util.LogsConstants.WRONG_START_DATE;
import static school.faang.user_service.util.LogsConstants.WRONG_USER_ID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidEndDate
public class EventCreateRequest implements EndDateValidatable {
    @NotBlank(message = NULL_TITLE)
    @Size(min = 3, max = 255, message = TEXT_LIMIT_FROM_3_TO_255)
    private String title;

    @Size(max = 2000, message = TEXT_LIMIT_TO_2000)
    private String description;

    @NotNull(message = NULL_START_DATE)
    @FutureOrPresent(message = WRONG_START_DATE)
    private LocalDateTime startDate;

    @Future(message = WRONG_END_DATE)
    private LocalDateTime endDate;

    @Size(max = 255, message = TEXT_LIMIT_FROM_3_TO_255)
    private String location;

    @Positive(message = NOT_POSITIVE_NUMBER)
    private Integer maxAttendees;

    @NotNull(message = NULL_ID)
    @Positive(message = WRONG_USER_ID)
    private Long ownerId;

    private EventType eventType;

    private EventStatus eventStatus;

    private List<Long> relatedSkills;
}
