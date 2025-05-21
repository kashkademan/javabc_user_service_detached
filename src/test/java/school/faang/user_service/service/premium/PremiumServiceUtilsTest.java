package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.utils.PremiumServiceUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceUtilsTest {
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private PremiumServiceUtils premiumServiceUtils;

    @Test
    void shouldThrowExceptionWhenUserHasNoPremium() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);

        assertThrows(RuntimeException.class, () -> premiumServiceUtils.isUserHasNoPremium(1L));
    }

    @Test
    void shouldThrowExceptionWhenPremiumExpired() {
        User user = new User();
        Premium premium = Premium.builder()
                .endDate(LocalDateTime.now().minusDays(1))
                .build();
        user.setPremium(premium);
        when(userService.getUserById(1L)).thenReturn(user);

        assertThrows(RuntimeException.class, () -> premiumServiceUtils.isUserHasNoPremium(1L));
    }

    @Test
    void shouldAssignPremiumToUser() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);
        when(premiumRepository.save(any())).thenReturn(new Premium());

        premiumServiceUtils.assignPremiumToUser(1L, 30);

        verify(premiumRepository, times(1)).save(any());
        assertNotNull(user.getPremium());
    }
}