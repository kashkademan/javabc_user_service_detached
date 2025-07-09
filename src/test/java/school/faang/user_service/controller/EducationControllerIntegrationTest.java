package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.service.education.EducationService;

@SpringBootTest
@AutoConfigureMockMvc
public class EducationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EducationService educationService;

    @Test
    public void getEducationById_successResult() throws Exception {
        EducationDto education = new EducationDto( 2000, 2005, "inst1",
                "middle", "spec1");
        Mockito.when(educationService.getEducationById(1L)).thenReturn(education);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/education?educationId=2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.yearTo").value(2003));

    }
}
