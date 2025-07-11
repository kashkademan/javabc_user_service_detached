package school.faang.user_service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.premium.PremiumController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.UserWithPremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumServiceImpl;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тесты контроллера премиум-подписок")
@WebMvcTest(PremiumController.class)
class PremiumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PremiumServiceImpl premiumService;

    @MockBean
    private UserContext userContext;

    @Test
    @DisplayName("Покупка премиума возвращает 200 при корректном количестве дней")
    void buyPremium_shouldReturnOk_whenValidDays() throws Exception {
        long userId = 1L;
        PremiumDto dto = PremiumDto.builder()
                .userId(userId)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .build();

        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(eq(userId), eq(PremiumPeriod.ONE_MONTH))).thenReturn(dto);

        mockMvc.perform(post("/premium/buy")
                        .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value((int) userId));
    }

    @Test
    @DisplayName("Покупка премиума с некорректным количеством дней возвращает 400")
    void buyPremium_shouldReturnBadRequest_whenInvalidDays() throws Exception {
        mockMvc.perform(post("/premium/buy")
                        .param("days", "7"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получение пользователей с активной подпиской возвращает список")
    void getUsersWithActivePremium_shouldReturnList() throws Exception {
        UserWithPremiumDto dto = new UserWithPremiumDto();
        dto.setId(1L);

        when(premiumService.getUsersWithActivePremium())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/premium/active-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
