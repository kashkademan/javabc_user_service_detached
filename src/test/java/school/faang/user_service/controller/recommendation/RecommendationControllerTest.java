package school.faang.user_service.controller.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RecommendationControllerTest — тест для контроллера {@link RecommendationController}.
 *
 * @author bozya
 * @since 24.07.2025
 */
@WebMvcTest(RecommendationController.class)
public class RecommendationControllerTest {

    @MockBean
    private UserContext userContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("/POST /recommendations -> 201 created")
    void testCreateSuccessful() throws Exception {
        Long authorId = 1L;
        Long receiverId = 2L;
        String content = "content";

        RecommendationCreateDto request = RecommendationCreateDto.builder()
                .authorId(authorId)
                .receiverId(receiverId)
                .content(content)
                .build();

        RecommendationViewDto responseDto = new RecommendationViewDto(1L, authorId, receiverId, content, null);

        when(recommendationService.create(any(RecommendationCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.receiverId").value(receiverId))
                .andExpect(jsonPath("$.content").value(content));
    }

    @Test
    @DisplayName("/PUT /recommendations/{id} -> 200 OK")
    void testUpdateSuccessful() throws Exception {
        Long recommendationId = 1L;
        Long authorId = 1L;
        Long receiverId = 2L;
        String updatedContent = "updated";

        RecommendationViewDto responseDto = new RecommendationViewDto(
                recommendationId,
                authorId,
                receiverId,
                updatedContent,
                null
        );

        when(recommendationService.update(eq(recommendationId), any(RecommendationUpdateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/recommendations/{recommendationId}", recommendationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "authorId": 1,
                        "receiverId": 2,
                        "content": "updated"
                    }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recommendationId))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.receiverId").value(receiverId))
                .andExpect(jsonPath("$.content").value(updatedContent));
    }

    @Test
    @DisplayName("/DELETE /recommendations/delete/{id} -> 204 No Content")
    void testDeleteSuccess() throws Exception {
        Long recommendationId = 1L;

        doNothing().when(recommendationService).delete(recommendationId);

        mockMvc.perform(delete("/recommendations/{recommendationId}", recommendationId))
                .andExpect(status().isNoContent());

        verify(recommendationService, times(1)).delete(recommendationId);
    }

    @Test
    @DisplayName("GET /recommendations?authorId=1 -> 200 OK with filtered recommendations")
    void testGetByFiltersSuccess() throws Exception {
        Long authorId = 1L;
        RecommendationFilterDto filters = new RecommendationFilterDto(null, authorId, null);
        List<RecommendationViewDto> expectedRecommendations = List.of(
                new RecommendationViewDto(1L, authorId, 2L, "Content 1", null),
                new RecommendationViewDto(2L, authorId, 3L, "Content 2", null)
        );

        when(recommendationService.getByFilters(any(RecommendationFilterDto.class)))
                .thenReturn(expectedRecommendations);

        mockMvc.perform(get("/recommendations")
                        .param("authorId", authorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].authorId").value(authorId))
                .andExpect(jsonPath("$[1].authorId").value(authorId));

        verify(recommendationService, times(1)).getByFilters(filters);
    }
}