package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.goal.*;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalController.class)
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GoalService goalService;

    @MockBean
    private UserContext userContext;

    private CreateGoalDto createGoalDto;
    private UpdateGoalDto updateGoalDto;
    private GoalDto goalDto;
    private GoalFilterDto filterDto;

    @BeforeEach
    void setUp() {
        createGoalDto = new CreateGoalDto(
                "Title",
                "Description",
                LocalDateTime.now().plusDays(1),
                1L,
                List.of(2L, 3L)
        );

        updateGoalDto = new UpdateGoalDto(
                "Updated Title",
                "Updated Description",
                LocalDateTime.now().plusDays(2),
                1L,
                GoalStatus.ACTIVE
        );

        goalDto = new GoalDto(
                "Title",
                "Description",
                LocalDateTime.now().plusDays(1),
                1L,
                List.of(2L, 3L),
                GoalStatus.ACTIVE
        );

        filterDto = new GoalFilterDto(
                "Title",
                "Description",
                GoalStatus.ACTIVE,
                1L
        );
    }

    @Test
    void testCreateGoal() throws Exception {
        Mockito.when(goalService.create(any(CreateGoalDto.class))).thenReturn(goalDto);

        mockMvc.perform(post("/api/v1/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGoalDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.mentorId").value(1));
    }

    @Test
    void testUpdateGoal() throws Exception {
        Mockito.when(goalService.update(eq(1L), any(UpdateGoalDto.class))).thenReturn(goalDto);

        mockMvc.perform(put("/api/v1/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateGoalDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.mentorId").value(1));
    }

    @Test
    void testDeleteGoal() throws Exception {
        mockMvc.perform(delete("/api/v1/goals/1"))
                .andExpect(status().isOk());

        Mockito.verify(goalService).deleteGoal(1L);
    }

    @Test
    void testDeleteGoalFromUser() throws Exception {
        mockMvc.perform(delete("/api/v1/goals/users/1/2"))
                .andExpect(status().isOk());

        Mockito.verify(goalService).deleteGoalFromUser(1L, 2L);
    }

    @Test
    void testGetByFilters() throws Exception {
        Mockito.when(goalService.getByFilters(any(GoalFilterDto.class))).thenReturn(List.of(goalDto));

        mockMvc.perform(get("/api/v1/goals")
                        .param("titleContains", filterDto.titleContains())
                        .param("descriptionContains", filterDto.descriptionContains())
                        .param("status", filterDto.status().name())
                        .param("mentorId", String.valueOf(filterDto.mentorId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title"))
                .andExpect(jsonPath("$[0].mentorId").value(1));
    }
}
