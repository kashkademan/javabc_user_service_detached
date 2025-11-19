package school.faang.user_service.controller.goal;

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
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
    private Country country;

    @BeforeEach
    void setUp() {
        goalRepository.deleteAll();
        userRepository.deleteAll();
        countryRepository.deleteAll();

        country = new Country();
        country.setTitle("TestСountry_");
        countryRepository.save(country);

        mentor = User.builder()
                .username("mentor_user")
                .email("mentor@test.com")
                .password("password123")
                .country(country)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(mentor);

        user1 = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password123")
                .country(country)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user1);

        user2 = User.builder()
                .username("user2")
                .email("user2@test.com")
                .password("password123")
                .country(country)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user2);

        goal = new Goal();
        goal.setTitle("Start title");
        goal.setDescription("Start description");
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setMentor(mentor);
        goal.setUsers(new ArrayList<>(List.of(user1, user2)));
        goalRepository.save(goal);
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

    @Test
    void update_withValidData_shouldUpdateGoal() throws Exception {

        Long currentUserId = mentor.getId();
        GoalUpdateDto goalUpdateDto = new GoalUpdateDto("update Title", "update Desc", null,
                null, null, null);

        Long goalId = goal.getId();

        mockMvc.perform(patch("/goals/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", currentUserId.toString())
                        .content(objectMapper.writeValueAsString(goalUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("update Title"))
                .andExpect(jsonPath("$.description").value("update Desc"));

    }

    @Test
    void delete_withValidData_shouldDeleteGoal() throws Exception {
        Long goalId = goal.getId();
        Long currentUserId = mentor.getId();

        mockMvc.perform(delete("/goals/{goalId}", goalId)
                        .header("x-user-id", currentUserId.toString()))
                .andExpect(status().isOk());

        assertThat(goalRepository.findById(goalId)).isEmpty();
    }

    @Test
    void filters_withValidData_shouldFiltersGoal() throws Exception {
        Long currentUserId = mentor.getId();
        GoalFilterDto goalFilterDto = new GoalFilterDto("Start title",
                null,
                null,
                null,
                null);

        MvcResult result = mockMvc.perform(post("/goals/filters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", currentUserId.toString())
                        .content(objectMapper.writeValueAsString(goalFilterDto)))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Actual status: " + result.getResponse().getStatus());
        System.out.println("Response body: " + result.getResponse().getContentAsString());

        List<GoalDto> goalDtos = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, GoalDto.class));


        assertThat(goalDtos.size()).isEqualTo(1);
    }
}
