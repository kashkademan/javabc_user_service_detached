package school.faang.user_service.controller.skill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Проверка контроллера по работе с навыками пользователей")
public class SkillControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SkillService service;
    @MockBean
    private UserContext userContext;

    long skillId = 1L;
    long userId = 2L;
    String title = "title";
    int offersAmount = 3;

    @Test
    @DisplayName("Проверка успешного добавление навыка")
    void testCreateSkill_WhenValidInput() throws Exception {
        SkillCreateDto skillCreateDto = new SkillCreateDto("title");
        SkillViewDto skillViewDto = new SkillViewDto(1L, "title");
        when(service.create(skillCreateDto)).thenReturn(skillViewDto);

        mockMvc.perform(post("/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(skillViewDto)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("title"));

        verify(service).create(skillCreateDto);
    }

    @Test
    @DisplayName("Проверка ошибки при добавления навыка с пустым названием навыка")
    void testCreateSkill_WhenTitleIsBlank() throws Exception {
        SkillCreateDto skillCreateDto = new SkillCreateDto("");

        mockMvc.perform(post("/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка ошибки при добавления навыка с null значением названия навыка")
    void testCreateSkill_WhenTitleIsNull() throws Exception {
        SkillCreateDto skillCreateDto = new SkillCreateDto(null);

        mockMvc.perform(post("/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка успешного получения списка навыков по Id пользователя ")
    void testGetByUserId_WhenSkillListIsNotEmpty() throws Exception {
        SkillViewDto skillViewDto = new SkillViewDto(skillId, title);
        List<SkillViewDto> skillViewDtoList = List.of(skillViewDto);
        when(service.getByUserId(userId)).thenReturn(skillViewDtoList);

        mockMvc.perform(get("/skills/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(skillId))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @Test
    @DisplayName("Проверка получения пустого списка навыков у пользователя")
    void testGetByUserId_ShouldReturnEmptyList() throws Exception {
        when(service.getByUserId(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/skills/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Проверка ошибки поиска навыков из-за отсутвия пользователя в базе данный")
    void testGetByUserId_WhenNoUserInDataBase() throws Exception {
        when(service.getByUserId(anyLong()))
                .thenThrow(new EntityNotFoundException("нет пользователя с таким Id в базе данных"));

        mockMvc.perform(get("/skills/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Проверка успешного получения списка рекомендованных навыков пользователю")
    void testGetOfferedSkills_WhenSkillOfferedListIsNotEmpty() throws Exception {
        SkillViewDto skillViewDto = new SkillViewDto(skillId, title);
        SkillOfferDto skillOfferDto = new SkillOfferDto(skillViewDto, offersAmount);
        when(service.getOfferedSkills()).thenReturn(List.of(skillOfferDto));

        mockMvc.perform(get("/skills/offered")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].skill.id").value(skillId))
                .andExpect(jsonPath("$[0].skill.title").value(title))
                .andExpect(jsonPath("$[0].offersAmount").value(offersAmount));
    }

    @Test
    @DisplayName("Проверка ошибки при получении пустого списка рекомендованных навыков")
    void testGetOfferedSkills_WhenSkillOfferedListIsEmpty() throws Exception {
        when(service.getOfferedSkills()).thenReturn(List.of());

        mockMvc.perform(get("/skills/offered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

    }

    @Test
    @DisplayName("Проверка ошибки поиска рекомендованных навыков из-за отсутвия пользователя в базе данный")
    void testGetOfferedSkills_WhenNoUserInDataBase() throws Exception {
        when(service.getOfferedSkills())
                .thenThrow(new EntityNotFoundException("нет пользователя с таким Id в базе данных"));

        mockMvc.perform(get("/skills/offered"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Проверка успешного добавления навыка пользователю")
    void testAcquireSkillFromOffers_WhenValidInput() throws Exception {
        mockMvc.perform(put("/skills/{skillId}", skillId))
                .andExpect(status().isNoContent());
        verify(service).acquireSkillFromOffers(skillId);
    }

    @Test
    @DisplayName("Проверка ошибки при добавлении навыка пользователю, потому что навык не найден")
    void testAcquireSkillFromOffers_WhenSkillNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Добавляемый навык не найден"))
                .when(service).acquireSkillFromOffers(skillId);

        mockMvc.perform(put("/skills/{skillId}", skillId))
                .andExpect(status().isNotFound());
    }
}
