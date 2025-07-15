package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    private static GoalDto defRespBody;

    @BeforeAll
    static void setUp() {
        defRespBody = new GoalDto(
                1L,
                null,
                "Spring boot test",
                "Use @SpringBootTest for test",
                GoalStatus.ACTIVE,
                LocalDateTime.now().plusDays(1),
                null,
                List.of(1L, 2L, 3L)
        );
    }

    static Stream<Arguments> provideCreateParams() {
        var reqBody = new GoalCreateDto(
                defRespBody.parentId(),
                defRespBody.title(),
                defRespBody.description(),
                defRespBody.deadline(),
                defRespBody.mentorId(),
                defRespBody.userIds()
        );
        return Stream.of(Arguments.of(reqBody, defRespBody));
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
    void update_success(long goalId, GoalUpdateDto reqDto, GoalDto respBody) throws Exception {
        when(goalService.update(eq(goalId), any(GoalUpdateDto.class)))
                .thenReturn(respBody);
        mockMvc.perform(put("/goals/" + goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(reqDto)))
                .andExpect(content().json(objMapper.writeValueAsString(respBody)))
                .andExpect(status().isOk());
    }

    static Stream<Arguments> provideGetByIdParams() {
        var goalId = defRespBody.id();
        return Stream.of(Arguments.of(goalId, defRespBody));
    }

    @ParameterizedTest
    @MethodSource("provideGetByIdParams")
    @DisplayName("get goal by id - success case")
    void getById_success(long goalId, GoalDto respBody) throws Exception {
        when(goalService.getById(eq(goalId)))
                .thenReturn(respBody);
        mockMvc.perform(get("/goals/" + goalId))
                .andExpect(content().json(objMapper.writeValueAsString(respBody)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("success ")
    void delete_success() throws Exception {
        var goalId = 1L;
        doNothing().when(goalService).delete(eq(goalId));
        mockMvc.perform(delete("/goals/" + goalId))
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(status().isOk());
        verify(goalService, times(1)).delete(goalId);
    }

    static Stream<Arguments> provideGetListParams() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("titleContains", "Spring");
        return Stream.of(Arguments.of(map, List.of(defRespBody)));
    }

    @ParameterizedTest
    @MethodSource("provideGetListParams")
    @DisplayName("get List - success case")
    void getList_success(MultiValueMap<String, String> params, List<GoalDto> respBody) throws Exception {
        when(goalService.getByFilters(any(GoalFilterDto.class)))
                .thenReturn(respBody);
        mockMvc.perform(get("/goals/search")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .params(params))
                .andExpect(content().json(objMapper.writeValueAsString(respBody)))
                .andExpect(status().isOk());
    }
}