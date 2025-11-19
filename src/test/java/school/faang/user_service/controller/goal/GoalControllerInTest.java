package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.ApplicationContextTest;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GoalControllerInTest extends ApplicationContextTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_withValidData_shouldReturnGoalDto() throws Exception {
        Long currentUserId = 1L;
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Test Goal Title",
                "Test Goal Description",
                LocalDateTime.now().plusMonths(1),
                1L,
                List.of(2L, 3L),
                List.of(1L, 3L)
        );

        mockMvc.perform(post("/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", currentUserId.toString())
                        .content(objectMapper.writeValueAsString(createGoalDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Goal Title"))
                .andExpect(jsonPath("$.description").value("Test Goal Description"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

}
