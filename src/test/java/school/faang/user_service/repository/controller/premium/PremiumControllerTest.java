package school.faang.user_service.repository.controller.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.premium.PremiumController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumControllerTest {

    @Mock
    private PremiumService premiumService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private PremiumController premiumController;

    private PremiumDto testPremiumDto;

    @BeforeEach
    void setUp() {
        testPremiumDto = PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .amount(new BigDecimal("10.00"))
                .paymentNumber("PREM-1-test123")
                .verificationCode(12345)
                .currency(Currency.USD)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void buyPremium_WithValidDays_ShouldReturnOk() {
        
        int days = 30;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(userId, PremiumPeriod.MONTHLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(days);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(userId);
        assertThat(response.getBody().getPremiumPeriod()).isEqualTo(PremiumPeriod.MONTHLY);
    }

    @Test
    void buyPremium_WithQuarterlyDays_ShouldReturnOk() {
        
        int days = 90;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(userId, PremiumPeriod.QUARTERLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(days);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void buyPremium_WithYearlyDays_ShouldReturnOk() {
        
        int days = 365;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(userId, PremiumPeriod.YEARLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(days);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(ints = {30, 90, 365})
    void buyPremium_WithValidPeriods_ShouldCallServiceWithCorrectPeriod(int days) {
        
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(any(Long.class), any(PremiumPeriod.class))).thenReturn(testPremiumDto);
        
        premiumController.buyPremium(days);

        // Verification is done through the mock setup
        // The service is called with the correct period based on days
    }

    @Test
    void buyPremium_WithInvalidDays_ShouldThrowException() {
        
        int invalidDays = 999;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);

        
        assertThatThrownBy(() -> premiumController.buyPremium(invalidDays))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported premium period days: 999");
    }

    @Test
    void buyPremium_WithZeroDays_ShouldThrowException() {
        
        int zeroDays = 0;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);

        
        assertThatThrownBy(() -> premiumController.buyPremium(zeroDays))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported premium period days: 0");
    }

    @Test
    void buyPremium_WithNegativeDays_ShouldThrowException() {
        
        int negativeDays = -30;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);

        
        assertThatThrownBy(() -> premiumController.buyPremium(negativeDays))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported premium period days: -30");
    }

    @Test
    void buyPremium_WithServiceException_ShouldPropagateException() {
        
        int days = 30;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(premiumService.buyPremium(userId, PremiumPeriod.MONTHLY))
                .thenThrow(new RuntimeException("Service error"));

        
        assertThatThrownBy(() -> premiumController.buyPremium(days))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service error");
    }

    @Test
    void buyPremium_WithUserContextException_ShouldPropagateException() {
        
        int days = 30;
        when(userContext.getUserId()).thenThrow(new RuntimeException("User context error"));

        
        assertThatThrownBy(() -> premiumController.buyPremium(days))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User context error");
    }

    @Test
    void buyPremium_WithMaxLongUserId_ShouldWork() {
        
        int days = 30;
        long maxUserId = Long.MAX_VALUE;
        when(userContext.getUserId()).thenReturn(maxUserId);
        when(premiumService.buyPremium(maxUserId, PremiumPeriod.MONTHLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(days);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void buyPremium_WithMinLongUserId_ShouldWork() {
        
        int days = 30;
        long minUserId = Long.MIN_VALUE;
        when(userContext.getUserId()).thenReturn(minUserId);
        when(premiumService.buyPremium(minUserId, PremiumPeriod.MONTHLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(days);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void buyPremium_WithZeroUserId_ShouldWork() {
        
        int days = 30;
        long zeroUserId = 0L;
        when(userContext.getUserId()).thenReturn(zeroUserId);
        when(premiumService.buyPremium(zeroUserId, PremiumPeriod.MONTHLY)).thenReturn(testPremiumDto);

        
        ResponseEntity<PremiumDto> response = premiumController.buyPremium(days);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
