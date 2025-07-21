package school.faang.user_service.controller.skill;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillServiceImpl;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты SkillController")
public class SkillControllerTest {

    @Mock
    private SkillServiceImpl skillServiceMock;

    @Mock
    private UserContext userContextMock;

    @InjectMocks
    private SkillController skillController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(skillController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @DisplayName("Post /api/v1/skill - Успешное создания скилла")
    @ParameterizedTest(name = "[{index}] {0}")
    @ValueSource(strings = {"Java", "Python", "JavaScript", "C++", "Go"})
    public void createSkill_ReturnsCreatedSkill(String inputData) throws Exception {
        SkillDto responseDto = new SkillDto(1L, inputData);

        when(skillServiceMock.create(any(CreateSkillDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + inputData + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(inputData))
                .andExpect(jsonPath("$.id").exists());
        verify(skillServiceMock, times(1)).create(any(CreateSkillDto.class));
    }

    @Test
    @DisplayName("Post /api/v1/skill - Ошибка при создании скилла")
    public void createSkill_ReturnsBadRequest() throws Exception {

        mockMvc.perform(post("/api/v1/skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}")) // пустой заголовок
                .andExpect(status().isBadRequest());
        verify(skillServiceMock, never()).create(any(CreateSkillDto.class));
    }

    @Test
    @DisplayName("Get /api/v1/skill - Получение скиллов пользователя")
    public void getSkillsByUserId_ReturnsSkills() throws Exception {
        long userId = 1L;
        SkillDto skillDto = new SkillDto(1L, "Java");

        when(skillServiceMock.getByUserId(userId)).thenReturn(List.of(skillDto));

        mockMvc.perform(get("/api/v1/skill/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].id").value(1));
        verify(skillServiceMock, times(1)).getByUserId(userId);
    }

    @Test
    @DisplayName("Post /api/v1/skill/offers - Получение предложенных скиллов")
    public void acquireSkillFromOffers_ReturnsOk() throws Exception {
        long skillId = 1L;
        long userId = 1L;

        when(userContextMock.getUserId()).thenReturn(userId);
        doNothing().when(skillServiceMock).acquireSkillFromOffers(skillId, userId);

        mockMvc.perform(post("/api/v1/skill/acquire/{skillId}", skillId)
                        .header("User-Id", userId))
                .andExpect(status().isOk());

        verify(skillServiceMock, times(1)).acquireSkillFromOffers(skillId, userId);
    }

}