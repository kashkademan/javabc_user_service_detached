package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.dto.goal.GoalInvitationViewDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("GoalInvitationController test")
@WebMvcTest(GoalInvitationController.class)
class GoalInvitationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserContext userContext;

    @MockBean
    private GoalInvitationService service;

    @Autowired
    private ObjectMapper objMapper;

    private static GoalInvitationViewDto defResp;

    @BeforeAll
    static void setUp() {
        var inviter = new UserDto(
                1L,
                "Myrzakhmet",
                "example@example.com",
                "+77477477474",
                "Java Dev"
        );
        var invited = new UserDto(
                2L,
                "Dreamer",
                "example2@example.com",
                "+77377377373",
                "Dreamer"
        );
        defResp = new GoalInvitationViewDto(1L, inviter, invited, RequestStatus.PENDING);
    }

    @Test
    @DisplayName("create goal invitation - success case")
    void create_success() throws Exception {
        var goalId = 1L;
        var createDto = new GoalInvitationCreateDto(2L);
        when(service.create(goalId, createDto))
                .thenReturn(defResp);
        mockMvc.perform(post("/goals-invitations/" + goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(createDto))
                ).andExpect(content().json(objMapper.writeValueAsString(defResp)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("accept goal invitation - success case")
    void accept_success() throws Exception {
        var goalId = 1L;
        mockMvc.perform(post("/goals-invitations/" + goalId + "/accept"))
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(status().isOk());
        verify(service).accept(goalId);
    }

    @Test
    @DisplayName("reject goal invitation - success case")
    void reject_success() throws Exception {
        var goalId = 1L;
        mockMvc.perform(post("/goals-invitations/" + goalId + "/reject"))
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(status().isOk());
        verify(service).reject(goalId);
    }

    @Test
    @DisplayName("get List of goal invitations - success case")
    void getList_success() throws Exception {
        var filterDto = new GoalInvitationFilterDto(1L, 2L, RequestStatus.PENDING);
        var invitationList = List.of(defResp);

        when(service.getByFilters(eq(filterDto)))
                .thenReturn(invitationList);

        mockMvc.perform(get("/goals-invitations/search")
                        .param("inviterId", filterDto.inviterId().toString())
                        .param("invitedId", filterDto.invitedId().toString())
                        .param("status", filterDto.status().toString()))
                .andExpect(content().json(objMapper.writeValueAsString(invitationList)))
                .andExpect(status().isOk());
    }
}