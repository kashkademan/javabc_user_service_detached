package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.payment.enums.Currency;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.common.PreConditionFailedException;
import school.faang.user_service.exception.payment.PaymentException;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private PremiumService premiumService;

    private User user;
    private PaymentResponse paymentResponse;
    private PaymentResponse failedResponse;
    private final int DAYS = 30;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        paymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                123,
                3L,
                new BigDecimal(10),
                Currency.USD,
                "");
        failedResponse = new PaymentResponse(
                PaymentStatus.FAILED,
                123,
                3L,
                new BigDecimal(10),
                Currency.USD,
                "");
    }
    @Test
    void testBuyPremiumWhenSuccess() {
        ResponseEntity<PaymentResponse> responseEntity = ResponseEntity.ok(paymentResponse);
        when(userService.getCurrentUser()).thenReturn(user);
        when(premiumRepository.existsByUserId(user.getId())).thenReturn(false);
        when(paymentServiceClient.sendPayment(any())).thenReturn(responseEntity);

        Premium result = premiumService.buyPremium(DAYS);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(DAYS, Duration.between(result.getStartDate(), result.getEndDate()).toDays());
        verify(premiumRepository).save(any(Premium.class));
    }

    @Test
    void testBuyPremiumWhenUserAlreadyHasPremium() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(premiumRepository.existsByUserId(user.getId())).thenReturn(true);

        assertThrows(PreConditionFailedException.class,() -> premiumService.buyPremium(DAYS));
    }

    @Test
    void testBuyPremiumWhenPaymentFails() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(premiumRepository.existsByUserId(user.getId())).thenReturn(false);
        when(paymentServiceClient.sendPayment(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        assertThrows(PaymentException.class, () -> premiumService.buyPremium(DAYS));
    }

    @Test
    void TestBuyPremiumWhenPaymentStatusNotSuccess() {
        ResponseEntity<PaymentResponse> response = ResponseEntity.ok(failedResponse);
        when(userService.getCurrentUser()).thenReturn(user);
        when(premiumRepository.existsByUserId(user.getId())).thenReturn(false);
        when(paymentServiceClient.sendPayment(any())).thenReturn(response);

        assertThrows(PaymentException.class, () -> premiumService.buyPremium(DAYS));
    }

    @Test
    void buyPremium_shouldThrowPaymentException_whenResponseIsNull() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(premiumRepository.existsByUserId(user.getId())).thenReturn(false);
        when(paymentServiceClient.sendPayment(any())).thenReturn(null);

        assertThrows(PaymentException.class, () -> premiumService.buyPremium(DAYS));
    }

    @Test
    void buyPremium_shouldThrowPaymentException_whenResponseBodyIsNull() {
        ResponseEntity<PaymentResponse> responseWithNullBody = ResponseEntity.ok(null);
        when(userService.getCurrentUser()).thenReturn(user);
        when(premiumRepository.existsByUserId(user.getId())).thenReturn(false);
        when(paymentServiceClient.sendPayment(any())).thenReturn(responseWithNullBody);

        assertThrows(PaymentException.class, () -> premiumService.buyPremium(DAYS));
    }
}
