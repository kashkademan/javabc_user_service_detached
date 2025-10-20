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
    public static final LocalDateTime START_DATE = LocalDateTime.now().plusDays(1);
    public static final LocalDateTime END_DATE = LocalDateTime.now().plusDays(2);
    public static final String TITLE = "Тестовое событие";
    public static final String DESCRIPTION = "Описание тестового события";


    public static CreateEventDto createEventDto() {
        return new CreateEventDto(TITLE, DESCRIPTION, START_DATE, END_DATE, EventType.WEBINAR);
    }

    public static User createUser(Long id) {
        return User.builder().id(id).build();
    }

    public static Event createEvent(Long id, User owner) {
        return Event.builder()
                .id(id)
                .title(TITLE)
                .description(DESCRIPTION)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .type(EventType.WEBINAR)
                .owner(owner)
                .status(EventStatus.PLANNED)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
