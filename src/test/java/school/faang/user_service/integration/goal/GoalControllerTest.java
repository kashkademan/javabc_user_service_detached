package school.faang.user_service.integration.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class GoalControllerTest {

    private static final String GET_URL = "/api/v1/goals/{id}";

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GoalRepository goalRepository;

    @BeforeEach
    void setUp() {
        goalRepository.deleteAll();
    }

    @Test
    void testGetGoalById() throws Exception {
        String goalName = "Test goal";
        String goalDescription = "Learn spring";
        Goal goal = Goal.builder()
                .title(goalName)
                .description(goalDescription)
                .status(GoalStatus.ACTIVE)
                .build();
        goal = goalRepository.save(goal);

        mockMvc.perform(get(GET_URL, goal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goal.getId()))
                .andExpect(jsonPath("$.title").value(goalName))
                .andExpect(jsonPath("$.description").value(goalDescription))
                .andExpect(jsonPath("$.status").value(GoalStatus.ACTIVE.name()));
    }

    @Test
    void testGetNotExistingGoal() throws Exception {
        Long id = 1L;
        String errorMessage = "Goal with id %d not exist";

        mockMvc.perform(get(GET_URL, id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage.formatted(id)));
    }
}