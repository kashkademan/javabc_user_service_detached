package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    private EventViewDto viewDto;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setup() {
        startDate = LocalDateTime.now().plusDays(1);
        endDate = LocalDateTime.now().plusDays(2);

        viewDto = new EventViewDto(
                1L,
                "Sample Event",
                "Description",
                startDate,
                endDate,
                EventType.WEBINAR,
                42L,
                EventStatus.PLANNED,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Создание события — успешный сценарий")
    void createEvent_success() throws Exception {

        EventCreateDto createDto = new EventCreateDto(
                "Sample Event",
                "Description",
                startDate,
                endDate,
                EventType.WEBINAR,
                new ArrayList<>(List.of(1L)),
                "Location A",
                EventStatus.PLANNED
        );

        Mockito.when(eventService.create(ArgumentMatchers.argThat(dto ->
                "Sample Event".equals(dto.getTitle())
                && "Description".equals(dto.getDescription())
                && startDate.equals(dto.getStartDate())
                && endDate.equals(dto.getEndDate())
                && EventType.WEBINAR.equals(dto.getType())
                && "Location A".equals(dto.getLocation())
                && EventStatus.PLANNED.equals(dto.getStatus())
        ))).thenReturn(viewDto);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(jsonPath("$.id").value(viewDto.getId()))
                .andExpect(jsonPath("$.title").value("Sample Event"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Обновление события — успешный сценарий")
    void updateEvent_success() throws Exception {
        EventUpdateDto updateDto = new EventUpdateDto(
                "Updated Event",
                "Updated Description",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                EventType.MEETING,
                EventStatus.PLANNED,
                "New Location"
        );

        EventViewDto updatedViewDto = new EventViewDto(
                1L,
                "Updated Event",
                "Updated Description",
                updateDto.getStartDate(),
                updateDto.getEndDate(),
                EventType.WEBINAR,
                42L,
                EventStatus.COMPLETED,
                LocalDateTime.now()
        );

        Mockito.when(eventService.update(ArgumentMatchers.eq(1L), ArgumentMatchers.any(EventUpdateDto.class)))
                .thenReturn(updatedViewDto);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Event"))
                .andExpect(jsonPath("$.type").value("WEBINAR"));
    }

    @Test
    @DisplayName("Получение списка событий — успешный сценарий")
    void getList_success() throws Exception {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, null);

        Mockito.when(eventService.getList(ArgumentMatchers.any(EventFilterDto.class)))
                .thenReturn(List.of(viewDto));

        mockMvc.perform(get("/events")
                        .param("titleContains", "")
                        .param("descriptionContains", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(viewDto.getId()));
    }

    @Test
    @DisplayName("Удаление события — успешный сценарий")
    void deleteEvent_success() throws Exception {
        Mockito.doNothing().when(eventService).delete(1L);

        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isNoContent());
    }
}
