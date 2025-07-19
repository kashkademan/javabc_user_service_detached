package school.faang.user_service.controller.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestViewDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестовый класс для проверки функциональности {@link RecommendationRequestController}
 * <p>
 * Проверяет корректность работы методов сервиса:
 * * <ul>
 * *     <li>Создание запроса на рекомендацию</li>
 * *     <li>Получение запроса по ID</li>
 * *     <li>Фильтрация запросов с различными параметрами</li>
 * *     <li>Принятие/отклонение запроса</li>
 * * </ul>
 * </p>
 *
 * @author Linempy
 * @since 18.07.2025
 */
@WebMvcTest(RecommendationRequestController.class)
public class RecommendationRequestControllerTest {
    private static final String REQUEST_MAPPING = "/recommendations";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RecommendationRequestService service;

    @MockBean
    private UserContext context;

    @Autowired
    private ObjectMapper objMapper;

    @InjectMocks
    RecommendationRequestController controller;

    @Test
    @DisplayName("/GET recommendations/10 -> 200 ")
    public void testGetByIdSuccessful() throws Exception {
        Long requestId = 10L;
        RecommendationRequestViewDto viewDto = new RecommendationRequestViewDto(
                requestId,
                "Проверь запрос",
                null,
                null,
                RequestStatus.PENDING,
                null,
                LocalDateTime.of(2020, 10, 10, 10, 10)
        );

        when(service.getById(eq(requestId))).thenReturn(viewDto);

        mockMvc.perform(get("/recommendations/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/GET /recommendations -> 200")
    public void testGetByFiltersSuccessful() throws Exception {
        when(service.getByFilters(any(RecommendationRequestFilterDto.class))).thenReturn(List.of(
                new RecommendationRequestViewDto(
                        1L,
                        "Test1",
                        new UserDto(1L, null, null, null, null),
                        null,
                        RequestStatus.PENDING,
                        null,
                        null),
                new RecommendationRequestViewDto(
                        2L,
                        "Test2",
                        null,
                        null,
                        RequestStatus.REJECTED,
                        null,
                        null)
        ));

        mockMvc.perform(get("/recommendations")
                        .param("requesterId", "1")
                        .param("status", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }


    @Test
    @DisplayName("/POST /recommendations -> 201 Created")
    public void testCreateSuccessful() throws Exception {
        RecommendationRequestCreateDto createDto = new RecommendationRequestCreateDto(
                "Сообщение", 1L, null
        );
        UserDto receiver = new UserDto(createDto.receiverId(), null, null, null, null);
        RecommendationRequestViewDto viewDto = new RecommendationRequestViewDto(
                1L, createDto.message(), null, receiver, RequestStatus.PENDING, null, null
        );

        when(service.create(any(RecommendationRequestCreateDto.class))).thenReturn(viewDto);

        mockMvc.perform(post("/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.message").value(createDto.message()))
                .andExpect(jsonPath("$.receiver.id").value(createDto.receiverId()));
    }

    @Test
    @DisplayName("/POST /recommendations/{id}/accept -> 200 OK, успешное принятие запроса")
    public void testAcceptSuccessful() throws Exception {
        long requesterId = 1L;
        doNothing().when(service).accept(requesterId);

        mockMvc.perform(post("/recommendations/{id}/accept", requesterId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> verify(service, times(1))
                        .accept(requesterId));
    }

    @Test
    @DisplayName("/POST /recommendations/{id}/reject -> 200 OK, успешное принятие запроса")
    public void testRejectSuccessful() throws Exception {
        long requesterId = 1L;
        RejectionDto rejectionDto = new RejectionDto(
                "Причина отказа"
        );

        doNothing().when(service).reject(requesterId, rejectionDto);

        mockMvc.perform(post("/recommendations/{id}/reject", requesterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(rejectionDto)))
                .andExpect(status().isOk())
                .andDo(result -> verify(service, times(1))
                        .reject(requesterId, rejectionDto));
    }
}

