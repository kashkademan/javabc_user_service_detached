package school.faang.event.mapper;

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
import school.faang.user_service.mapper.EventMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        Event event = mapper.toEntity(dto);

        assertNotNull(event);
        assertEquals("Java Meetup", event.getTitle());
        assertEquals("Learn advanced Java", event.getDescription());
        assertEquals(LocalDateTime.of(2025, 7, 15, 18, 0), event.getStartDate());
        assertEquals(LocalDateTime.of(2025, 7, 15, 20, 0), event.getEndDate());
        assertEquals(EventType.MEETING, event.getType());
        assertEquals("Amsterdam", event.getLocation());
        assertEquals(EventStatus.PLANNED, event.getStatus());
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

        EventViewDto dto = mapper.toViewDto(event);

        assertNotNull(dto);
        assertEquals(event.getId(), dto.getId());
        assertEquals("Tech Talk", dto.getTitle());
        assertEquals("Kotlin vs Java", dto.getDescription());
        assertEquals(LocalDateTime.of(2025, 8, 1, 10, 0), dto.getStartDate());
        assertEquals(LocalDateTime.of(2025, 8, 1, 12, 0), dto.getEndDate());
        assertEquals(EventType.WEBINAR, dto.getType());
        assertEquals(EventStatus.PLANNED, dto.getStatus());
        assertEquals(77L, dto.getOwnerId());
        assertEquals(LocalDateTime.of(2025, 7, 1, 9, 0), dto.getCreatedAt());
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

        assertEquals("Updated Event", event.getTitle());
        assertEquals("New description", event.getDescription());
        assertEquals(LocalDateTime.of(2025, 9, 1, 14, 0), event.getStartDate());
        assertEquals(LocalDateTime.of(2025, 9, 1, 16, 0), event.getEndDate());
        assertEquals(EventType.MEETING, event.getType());
        assertEquals(EventStatus.PLANNED, event.getStatus());
        assertEquals("London", event.getLocation());
    }
}