package school.faang.user_service.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService service;
    @MockBean
    private UserContext context;
    @Autowired
    private ObjectMapper objMapper;
    @MockBean
    private MentorshipService mentorshipService;

    private static UserDto user;

    @BeforeAll
    static void setUp() {
        user = new UserDto(
                1L,
                "JohnDoe",
                "johndoe@example.com",
                List.of(1L),
                "1234567890",
                "About John Doe",
                null
        );
    }

    @Test
    void create_success() throws Exception {
        var createDto = new UserCreateDto(
                "JohnDoe",
                "johndoe@example.com",
                "Mega_str0ng_passwd",
                1L,
                "TELEGRAM"
        );

        when(service.create(eq(createDto))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(createDto)))
                .andExpect(content().json(objMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        var updateDto = new UserUpdateDto(
                "JohnDoe",
                "johndoe@example.com",
                "1234567890",
                "About John Doe",
                1L,
                null
        );
        var userId = 1L;

        when(service.update(eq(userId), eq(updateDto)))
                .thenReturn(user);

        mockMvc.perform(put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(updateDto)))
                .andExpect(content().json(objMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }


    @Test
    void getById_success() throws Exception {
        var userId = 1L;
        when(service.getById(userId)).thenReturn(user);
        mockMvc.perform(get("/users/" + userId))
                .andExpect(content().json(objMapper.writeValueAsString(user)));
    }

    static Stream<Arguments> provideFilters() {
        var params1 = new LinkedMultiValueMap<String, String>();
        params1.add("onlyPremium", "false");
        params1.add("usernameContains", "John");
        var params2 = new LinkedMultiValueMap<String, String>();
        params2.add("onlyPremium", "true");
        var userWithoutPremium = new UserDto(
                3L,
                "MichaelJohnson",
                "michaeljohnson@example.com",
                List.of(1L),
                "1112223333",
                "About Michael Johnson",
                null
        );
        var userWithPremium = new UserDto(
                2L,
                "JaneSmith",
                "janesmith@example.com",
                List.of(1L),
                "0987654321",
                "About Jane Smith",
                null
        );
        var filter1 = new UserFilterDto("John", null, null, null, false);
        var filter2 = new UserFilterDto(null, null, null, null, true);
        var result1 = List.of(user, userWithoutPremium);
        var result2 = List.of(user, userWithPremium);
        return Stream.of(
                Arguments.of(filter1, params1, result1),
                Arguments.of(filter2, params2, result2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideFilters")
    void getUsers_success(UserFilterDto filter, MultiValueMap<String, String> params,
                          List<UserDto> users) throws Exception {
        when(service.getUsers(eq(filter))).thenReturn(users);
        mockMvc.perform(get("/users/search")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .params(params))
                .andExpect(content().json(objMapper.writeValueAsString(users)))
                .andExpect(status().isOk());
    }
}