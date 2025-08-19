package school.faang.user_service.controller.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.analytics.ProfileVisitViewDto;
import school.faang.user_service.service.analytics.ProfileVisitService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileVisitController.class)
public class ProfileVisitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objMapper;
    @MockBean
    private UserContext userContext;
    @MockBean
    private ProfileVisitService service;

    @Test
    @DisplayName("успешное получение списко посетителей страницы")
    public void getProfileVisitsSuccess() throws Exception {
        var now = LocalDateTime.now();
        var visitedId = 1L;
        var visitor = new ProfileVisitViewDto(1L, 2L, visitedId, now.minusHours(1));
        var visitor2 = new ProfileVisitViewDto(1L, 3L, visitedId, now);
        var visitors = List.of(visitor, visitor2);
        when(service.getUserVisitors(visitedId, 20, 0))
                .thenReturn(visitors);
        mockMvc.perform(get("/analytics/user/visits/" + visitedId))
                .andExpect(content().json(objMapper.writeValueAsString(visitors)))
                .andExpect(status().isOk());
    }
}
