package school.faang.user_service.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.user.GetUsersDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.service.user.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserDto firstUser = UserDto.builder()
            .id(22L)
            .username("antony")
            .build();
    private final UserDto secondUser = UserDto.builder()
            .id(23L)
            .username("bobik")
            .build();

    private MockMvc mockMvc;

    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetPremiumUsers() throws Exception {
        List<UserDto> expectedUsers = List.of(firstUser, secondUser);
        UserFiltersDto userFiltersDto = new UserFiltersDto("", "", 0, 0);

        when(userService.getPremiumUsers(userFiltersDto)).thenReturn(expectedUsers);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/premium")
                        .param("namePattern", userFiltersDto.namePattern())
                        .param("phonePattern", userFiltersDto.phonePattern())
                        .param("experienceMin", String.valueOf(userFiltersDto.experienceMin()))
                        .param("experienceMax", String.valueOf(userFiltersDto.experienceMax())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(expectedUsers.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> actualUsers = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));

        Assertions.assertTrue(actualUsers.containsAll(expectedUsers));
    }

    @Test
    void testGetUsersByIds() throws Exception {
        GetUsersDto getUsersDto = GetUsersDto.builder()
                .ids(new ArrayList<>(List.of(firstUser.id(), secondUser.id())))
                .build();
        List<UserDto> expectedUsers = List.of(firstUser, secondUser);

        when(userService.getUsersByIds(getUsersDto)).thenReturn(expectedUsers);

        String response = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getUsersDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(expectedUsers.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> actualUsers = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));

        Assertions.assertTrue(actualUsers.containsAll(expectedUsers));
    }

    @Test
    void testGetUser() throws Exception {
        when(userService.getById(firstUser.id())).thenReturn(firstUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", firstUser.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", Matchers.is(firstUser.id().intValue())))
                .andExpect(jsonPath("username", Matchers.is(firstUser.username())));
    }

    @Test
    void testDeactivateUser() throws Exception {
        long id = 1L;

        doNothing().when(userService).deactivateUser(id);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}/deactivate", id))
                .andExpect(status().isOk());

        verify(userService).deactivateUser(id);
    }

    @Test
    void testActivateUser() throws Exception {
        long id = 1L;

        doNothing().when(userService).activateUser(id);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}/activate", id))
                .andExpect(status().isOk());

        verify(userService).activateUser(id);
    }

    @Test
    void getNotBannedUsersIds() throws Exception {
        List<Long> userIds = List.of(firstUser.id());
        String[] ids = userIds.stream().map(String::valueOf).toArray(String[]::new);

        when(userService.getNotBannedUsersIds(userIds)).thenReturn(userIds);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/not-banned")
                        .queryParam("ids", ids))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(userIds.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Long> idsFromResponse = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));

        Assertions.assertTrue(userIds.containsAll(idsFromResponse));
    }
}