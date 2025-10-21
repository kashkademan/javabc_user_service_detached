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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.service.user.UserServiceImpl;

import java.util.List;

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
}