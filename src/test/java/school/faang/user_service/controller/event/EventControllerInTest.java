package school.faang.user_service.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import school.faang.user_service.ApplicationContextTest;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerInTest extends ApplicationContextTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User owner;
    private Event event;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
        countryRepository.deleteAll();

        Country country = new Country();
        country.setTitle("TestСountry_");
        countryRepository.save(country);

        owner = User.builder()
                .username("event_owner")
                .email("owner@test.com")
                .password("password123")
                .country(country)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(owner);

        event = Event.builder()
                .title("Start Event")
                .description("Start Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .type(EventType.WEBINAR)
                .owner(owner)
                .location("Bryansk")
                .status(EventStatus.PLANNED)
                .build();
        eventRepository.save(event);
    }

    @Test
    void create_withValidData_shouldReturnEventDto() throws Exception {
        Long currentUserId = owner.getId();
        EventCreateDto createDto = EventCreateDto.builder()
                .title("New Event")
                .description("New Description")
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(4))
                .type(EventType.WEBINAR)
                .skillsId(Set.of())
                .build();

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", currentUserId.toString())
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.title").value("New Event"),
                        jsonPath("$.description").value("New Description"),
                        jsonPath("$.type").value("WEBINAR"),
                        jsonPath("$.status").value("PLANNED")
            );
    }

    @Test
    void update_withValidData_shouldUpdateEvent() throws Exception {
        Long currentUserId = owner.getId();
        EventUpdateDto updateDto = EventUpdateDto.builder()
                .title("Updated Event")
                .description("Updated Description")
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(5))
                .type(EventType.WEBINAR)
                .status(EventStatus.PLANNED)
                .maxAttendees(50)
                .skillsId(Set.of())
                .build();

        mockMvc.perform(patch("/events/{eventId}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", currentUserId.toString())
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title").value("Updated Event"),
                        jsonPath("$.description").value("Updated Description")
            );
    }

    @Test
    void delete_withValidData_shouldDeleteEvent() throws Exception {
        Long currentUserId = owner.getId();
        Event event1 = eventRepository.findById(event.getId()).get();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + event1.getTitle());
        mockMvc.perform(delete("/events/{eventId}", event.getId())
                        .header("x-user-id", currentUserId.toString()))
                .andExpect(status().isNoContent());

        assertThat(eventRepository.findById(event.getId())).isEmpty();
    }

    @Test
    void filters_withValidData_shouldReturnFilteredEvents() throws Exception {
        Long currentUserId = owner.getId();
        EventFilterDto filterDto = EventFilterDto.builder()
                .titleContains("Start Event")
                .build();

        MvcResult result = mockMvc.perform(post("/events/filters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", currentUserId.toString())
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andReturn();

        List<EventDto> events = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EventDto.class));

        assertThat(events.size()).isEqualTo(1);
    }
}
