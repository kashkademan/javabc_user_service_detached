package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GoalControllerInTest extends ApplicationContextTest {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User mentor;
    private User user1;
    private User user2;
    private Goal goal;
    private Long mentorId;
    private Long userId1;
    private Long userId2;
    private Long goalId;
    private Country country;

    @BeforeEach
    void setUp() {
        goalRepository.deleteAll();
        userRepository.deleteAll();
        countryRepository.deleteAll();

        country = new Country();
        country.setTitle("TestСountry_" + UUID.randomUUID().toString().substring(0, 8));
        countryRepository.save(country);

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        mentorId = 1L;
        mentor = User.builder()
                .username("mentor_user" + uniqueId)
                .email("mentor" + uniqueId + "@test.com")
                .password("password123" + uniqueId)
                .country(country)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(mentor);

        userId1 = 2L;
        user1 = User.builder()
                .username("user1" + uniqueId)
                .email("user1" + uniqueId + "@test.com")
                .password("password123" + uniqueId)
                .country(country)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user1);

        userId2 = 3L;
        user2 = User.builder()
                .username("user2" + uniqueId)
                .email("user2" + uniqueId + "@test.com")
                .password("password123" + uniqueId)
                .country(country)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user2);

        goalId = 1L;
        goal = new Goal();
        goal.setTitle("Start title");
        goal.setDescription("Start description");
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setMentor(mentor);
        goal.setUsers(List.of(user1, user2));
    }

    @Test
    void create_withValidData_shouldReturnGoalDto() throws Exception {
        Long currentUserId = mentor.getId();
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Test Goal Title",
                "Test Goal Description",
                LocalDateTime.now().plusMonths(1),
                mentor.getId(),
                null,
                null
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
