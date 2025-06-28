package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.PaymentResponse;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.exception.AlreadyPremiumUserException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.messaging.publishers.PremiumBoughtEventPublisher;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceImplTest {
    private static final long USER_ID = 1L;
    private static final PremiumPeriod PREMIUM_PERIOD = PremiumPeriod.ONE_MONTH_PREMIUM;

    @Mock
    private UserService userService;

    @Mock
    private PremiumRepository premiumRepository;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumBoughtEventPublisher premiumBoughtEventPublisher;

    @Spy
    private UserContext userContext;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    @Test
    public void testBuyPremium_whenHasPremium_thenThrowException() {
        when(premiumRepository.existsByUserId(eq(USER_ID)))
                .thenReturn(true);

        assertThrows(AlreadyPremiumUserException.class,
                () -> premiumService.buyPremium(USER_ID, PREMIUM_PERIOD));
    }

    @Test
    public void testBuyPremium_whenResponseStatusNotSuccess_thenThrowException() {
        when(premiumRepository.existsByUserId(eq(USER_ID)))
                .thenReturn(false);
        when(userService.findUserById(eq(USER_ID)))
                .thenReturn(UserDto.builder().build());
        when(paymentServiceClient.sendPayment(any()))
                .thenReturn(PaymentResponse.builder().status(PaymentStatus.FAILURE).build());

        assertThrows(PaymentFailedException.class,
                () -> premiumService.buyPremium(USER_ID, PREMIUM_PERIOD));
        verify(premiumRepository, never()).save(any());
    }

    @Test
    public void testBuyPremium_whenValidParams_thenSuccessfulSave() {
        when(premiumRepository.existsByUserId(eq(USER_ID)))
                .thenReturn(false);
        when(userService.findUserById(eq(USER_ID)))
                .thenReturn(UserDto.builder().build());
        when(paymentServiceClient.sendPayment(any()))
                .thenReturn(PaymentResponse.builder().status(PaymentStatus.SUCCESS).build());
        when(premiumRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PremiumDto dto = premiumService.buyPremium(USER_ID, PREMIUM_PERIOD);

        assertNotNull(dto);
        verify(premiumRepository, times(1)).save(any());
    }
}