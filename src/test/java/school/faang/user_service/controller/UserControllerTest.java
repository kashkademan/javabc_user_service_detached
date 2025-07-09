package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserResponseDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.utils.Utils;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private static final Long USER_ID = 1L;

    private final Utils utils = new Utils();

    private MockMvc mockMvc;

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetUserByIdSuccess() throws Exception {
        String url = utils.format("/users/{}", USER_ID);

        Mockito.when(userService.getUserDtoById(USER_ID))
            .thenReturn(
                UserResponseDto.builder()
                    .id(USER_ID)
                    .username("userName")
                    .email("email@email.ru")
                    .build()
            );

        mockMvc.perform(get(url))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*", hasSize(7)))
            .andExpect(jsonPath("$.id").value(USER_ID));
    }
}