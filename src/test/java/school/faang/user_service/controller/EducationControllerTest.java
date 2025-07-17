package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.education.EducationController;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.service.education.EducationService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * EducationControllerTest — описание класса.
 * <p>
 * Тестирует класс EducationController
 * </p>*
 *
 * @author fomchenkoandrey
 * @since 13.07.2025
 */

@WebMvcTest(EducationController.class)
public class EducationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EducationService educationService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    private final long userId = 1L;
    private final long educationId = 100L;

    private EducationViewDto viewDto;

    @BeforeEach
    public void setUp() {
        viewDto = new EducationViewDto(
                educationId,
                2010,
                2014,
                "MIT",
                "Bachelor",
                "CS"
        );
    }

    @Test
    @DisplayName("Успешное добавление эндпоинта образования")
    void addEducationSecureTest() throws Exception {
        CreateEducationDto create = new CreateEducationDto(
                2010,
                2014,
                "MIT",
                "Bachelor",
                "CS"
        );
        when(userContext.getUserId()).thenReturn(userId);
        when(educationService.addEducation(userId, create)).thenReturn(viewDto);
        mockMvc.perform(post("/educations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(viewDto)));
    }

    @Test
    @DisplayName("Успешное обновление эндпоинта образования")
    void updateEducationSecureTest() throws Exception {
        UpdateEducationDto update = new UpdateEducationDto(
                2010,
                2015,
                "MIT",
                "Bachelor",
                "CS"
        );
        EducationViewDto viewDto = new EducationViewDto(
                educationId,
                2010,
                2014,
                "MIT",
                "Bachelor",
                "CS"
        );
        when(userContext.getUserId()).thenReturn(userId);
        when(educationService.updateEducation(userId, educationId, update)).thenReturn(viewDto);
        mockMvc.perform(put("/educations/{educationId}", educationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(viewDto)));
        verify(educationService).updateEducation(userId, educationId, update);
    }

    @Test
    @DisplayName("Успешное получение образования")
    void getByIdSecureTest() throws Exception {
        EducationViewDto viewDto = new EducationViewDto(
                educationId,
                2010,
                2014,
                "MIT",
                "Bachelor",
                "CS"
        );
        when(educationService.getById(educationId)).thenReturn(viewDto);
        mockMvc.perform(get("/educations/{educationId}", educationId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(viewDto)));
    }
}
