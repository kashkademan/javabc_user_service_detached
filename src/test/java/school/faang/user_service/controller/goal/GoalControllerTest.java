package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GoalController.class)
class GoalControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GoalService goalService;
    @MockBean
    private UserContext userContext;
    @Autowired
    private ObjectMapper objMapper;
    @Spy
    private GoalMapperImpl goalMapper;


    static Stream<Arguments> provideCreateParams() {
        var deadline = LocalDateTime.now().plusDays(1);
        var reqBody = new GoalCreateDto(
                null,
                "Spring boot test",
                "Use @SpringBootTest for test",
                deadline,
                null,
                List.of(1L, 2L, 3L)
        );
        var respBody = new GoalDto(
                1L,
                null,
                "Spring boot test",
                "Use @SpringBootTest for test",
                GoalStatus.ACTIVE,
                deadline,
                null,
                List.of(1L, 2L, 3L)
        );
        return Stream.of(Arguments.of(reqBody, respBody));
    }

    @ParameterizedTest
    @MethodSource("provideCreateParams")
    @DisplayName("create goal - success case")
    void create_success(GoalCreateDto reqBody, GoalDto respBody) throws Exception {
        when(goalService.create(any(GoalCreateDto.class)))
                .thenReturn(respBody);
        mockMvc.perform(post("/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(reqBody)))
                .andExpect(content().json(objMapper.writeValueAsString(respBody)))
                .andExpect(status().isOk());
    }

    static Stream<Arguments> provideUpdateParams() {
        var goalId = 1L;
        var deadline = LocalDateTime.now().plusDays(1);
        var reqBody = new GoalUpdateDto(
                "Spring boot test(Updated)",
                "IDK just use WebMvcTest",
                deadline,
                null,
                GoalStatus.COMPLETED
        );
        var respBody = new GoalDto(
                goalId,
                null,
                "Spring boot test(Updated)",
                "IDK just use WebMvcTest",
                GoalStatus.COMPLETED,
                deadline,
                null,
                List.of(1L, 2L, 3L)
        );
        return Stream.of(Arguments.of(goalId, reqBody, respBody));
    }

    @ParameterizedTest
    @MethodSource("provideUpdateParams")
    @DisplayName("update goal - success case")
    void update(long goalId, GoalUpdateDto reqDto, GoalDto respBody) throws Exception {
        when(goalService.update(eq(goalId), any(GoalUpdateDto.class)))
                .thenReturn(respBody);
        mockMvc.perform(put("/goals/" + goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(reqDto)))
                .andExpect(content().json(objMapper.writeValueAsString(respBody)))
                .andExpect(status().isOk());
    }

    @Test
    void getById() {
    }

    @Test
    void delete() {
    }

    @Test
    void getList() {
    }
}