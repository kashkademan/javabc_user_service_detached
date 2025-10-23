package school.faang.user_service.preparation.test;

import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;

public class PreparationTest {

    public static final long CURRENT_USER_ID = 1L;
    public static final long EVENT_ID = 100L;
    public static final long OTHER_USER_ID = 2L;
    public static final LocalDateTime NOW = LocalDateTime.now();
    public static final LocalDateTime PLUS_ONE_DAY = NOW.plusDays(1);
    public static final LocalDateTime PLUS_TWO_DAYS = NOW.plusDays(2);
    public static final String TITLE = "Тестовое событие";
    public static final String DESCRIPTION = "Описание тестового события";
    public static final LocalDateTime MINUS_DAYS_1 = NOW.minusDays(1);

    public static final User OWNER_1 = createUser(CURRENT_USER_ID);
    public static final Event EVENT_1 = createEvent(EVENT_ID, OWNER_1);
    public static final User OWNER_2 = createUser(OTHER_USER_ID);
    public static final Event EVENT_2 = createEvent(EVENT_ID, OWNER_2);

    public static CreateEventDto createEventDto() {
        return new CreateEventDto(TITLE, DESCRIPTION, PLUS_ONE_DAY, PLUS_TWO_DAYS, EventType.WEBINAR);
    }

    public static User createUser(Long id) {
        return User.builder().id(id).build();
    }

    public static Event createEvent(Long id, User owner) {
        return Event.builder()
                .id(id)
                .title(TITLE)
                .description(DESCRIPTION)
                .startDate(PLUS_ONE_DAY)
                .endDate(PLUS_TWO_DAYS)
                .type(EventType.WEBINAR)
                .owner(owner)
                .status(EventStatus.PLANNED)
                .createdAt(NOW)
                .build();
    }
}
