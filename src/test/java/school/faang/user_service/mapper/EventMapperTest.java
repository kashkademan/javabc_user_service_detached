package school.faang.user_service.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Юнит-тесты для EventMapper")
public class EventMapperTest {

    private final EventMapper mapper = Mappers.getMapper(EventMapper.class);

    @Test
    @DisplayName("toEntity: корректно маппит EventCreateDto в Event")
    void shouldMapCreateDtoToEntity() {
        EventCreateDto dto = new EventCreateDto();
        dto.setTitle("Java Meetup");
        dto.setDescription("Learn advanced Java");
        dto.setStartDate(LocalDateTime.of(2025, 7, 15, 18, 0));
        dto.setEndDate(LocalDateTime.of(2025, 7, 15, 20, 0));
        dto.setType(EventType.MEETING);
        dto.setLocation("Amsterdam");
        dto.setStatus(EventStatus.PLANNED);

        Event expected = new Event();
        expected.setTitle("Java Meetup");
        expected.setDescription("Learn advanced Java");
        expected.setStartDate(LocalDateTime.of(2025, 7, 15, 18, 0));
        expected.setEndDate(LocalDateTime.of(2025, 7, 15, 20, 0));
        expected.setType(EventType.MEETING);
        expected.setLocation("Amsterdam");
        expected.setStatus(EventStatus.PLANNED);

        Event actual = mapper.toEntity(dto);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("toViewDto: корректно маппит Event в EventViewDto с ownerId")
    void shouldMapEventToViewDto() {
        User owner = new User();
        owner.setId(77L);

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Tech Talk");
        event.setDescription("Kotlin vs Java");
        event.setStartDate(LocalDateTime.of(2025, 8, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 8, 1, 12, 0));
        event.setType(EventType.WEBINAR);
        event.setLocation("Berlin");
        event.setStatus(EventStatus.PLANNED);
        event.setCreatedAt(LocalDateTime.of(2025, 7, 1, 9, 0));
        event.setOwner(owner);

        EventViewDto actualDto = mapper.toViewDto(event);

        EventViewDto expectedDto = new EventViewDto(
                1L,
                "Tech Talk",
                "Kotlin vs Java",
                LocalDateTime.of(2025, 8, 1, 10, 0),
                LocalDateTime.of(2025, 8, 1, 12, 0),
                EventType.WEBINAR,
                77L,
                EventStatus.PLANNED,
                LocalDateTime.of(2025, 7, 1, 9, 0)
        );

        assertThat(actualDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("update: обновляет Event на основе EventUpdateDto")
    void shouldUpdateEventFromUpdateDto() {
        EventUpdateDto dto = new EventUpdateDto();
        dto.setTitle("Updated Event");
        dto.setDescription("New description");
        dto.setStartDate(LocalDateTime.of(2025, 9, 1, 14, 0));
        dto.setEndDate(LocalDateTime.of(2025, 9, 1, 16, 0));
        dto.setType(EventType.MEETING);
        dto.setStatus(EventStatus.PLANNED);
        dto.setLocation("London");

        Event event = new Event();
        event.setTitle("Old title");
        event.setDescription("Old desc");
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 12, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 1, 14, 0));
        event.setType(EventType.WEBINAR);
        event.setStatus(EventStatus.COMPLETED);
        event.setLocation("Paris");

        mapper.update(dto, event);

        Event expected = new Event();
        expected.setTitle("Updated Event");
        expected.setDescription("New description");
        expected.setStartDate(LocalDateTime.of(2025, 9, 1, 14, 0));
        expected.setEndDate(LocalDateTime.of(2025, 9, 1, 16, 0));
        expected.setType(EventType.MEETING);
        expected.setStatus(EventStatus.PLANNED);
        expected.setLocation("London");

        assertThat(event)
                .usingRecursiveComparison()
                .ignoringFields("id", "owner", "createdAt") // если есть
                .isEqualTo(expected);
    }
}