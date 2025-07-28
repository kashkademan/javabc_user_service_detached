package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.adapter.user.UserRepositoryAdapter;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.entity.premium.PremiumPeriod;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.CheckException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.impl.PremiumServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceImplTest {
    @InjectMocks
    private PremiumServiceImpl premiumServiceImpl;
    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private PremiumMapper premiumMapper;
    @Captor
    private ArgumentCaptor<Premium> premiumCaptor;

    private static final long USER_ID = 1L;
    private static final long PAYMENT_NUMBER = 999L;

    private User user;
    private PaymentResponse paymentSuccesResponse;
    private PaymentResponse paymentFailedResponse;
    private PremiumPeriod period;
    private List<Premium> testExpiredPremiums;

    @BeforeEach
    public void setUp() {
        user = new User();

        paymentSuccesResponse = new PaymentResponse();
        paymentSuccesResponse.setStatus(PaymentStatus.SUCCESS);

        paymentFailedResponse = new PaymentResponse();
        paymentFailedResponse.setStatus(PaymentStatus.FAILED);

        period = PremiumPeriod.ONE_MONTH;

        Premium expiredPremium1 = createExpiredPremium(1L, LocalDateTime.now().minusDays(5));
        Premium expiredPremium2 = createExpiredPremium(2L, LocalDateTime.now().minusDays(10));
        Premium expiredPremium3 = createExpiredPremium(3L, LocalDateTime.now().minusDays(1));

        testExpiredPremiums = Arrays.asList(expiredPremium1, expiredPremium2, expiredPremium3);
    }

    @Test
    public void testBuyPremium_successful() {
        Premium savedPremium = new Premium();
        PremiumDto savedPremiumDto = new PremiumDto();

        when(userRepositoryAdapter.getUserById(USER_ID)).thenReturn(user);
        when(premiumRepository.existsByUserIdAndEndDateGreaterThan(eq(USER_ID), any(LocalDateTime.class))).thenReturn(false);
        when(paymentServiceClient.pay(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<>(paymentSuccesResponse, HttpStatus.OK));
        when(premiumRepository.save(any(Premium.class))).thenReturn(savedPremium);
        when(premiumMapper.toDto(savedPremium)).thenReturn(savedPremiumDto);

        PremiumDto result = premiumServiceImpl.buyPremium(USER_ID, PAYMENT_NUMBER, period);

        assertNotNull(result);
        verify(userRepositoryAdapter, times(1)).getUserById(USER_ID);
        verify(premiumRepository, times(1)).existsByUserIdAndEndDateGreaterThan(eq(USER_ID), any(LocalDateTime.class));
        verify(paymentServiceClient, times(1)).pay(any(PaymentRequest.class));
        verify(premiumRepository, times(1)).save(premiumCaptor.capture());
        verify(premiumMapper, times(1)).toDto(savedPremium);
        assertEquals(savedPremiumDto, result);
        Premium capturedPremium = premiumCaptor.getValue();
        assertEquals(user, capturedPremium.getUser());
    }

    @Test
    public void testBuyPremium_userAlreadyHasPremium() {
        when(userRepositoryAdapter.getUserById(USER_ID)).thenReturn(user);
        when(premiumRepository.existsByUserIdAndEndDateGreaterThan(eq(USER_ID), any(LocalDateTime.class))).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> premiumServiceImpl.buyPremium(USER_ID, PAYMENT_NUMBER, period));

        verify(userRepositoryAdapter, times(1)).getUserById(USER_ID);
        verify(premiumRepository, times(1)).existsByUserIdAndEndDateGreaterThan(eq(USER_ID), any(LocalDateTime.class));
    }

    @Test
    public void testBuyPremium_paymentFailed() {
        when(userRepositoryAdapter.getUserById(USER_ID)).thenReturn(user);
        when(premiumRepository.existsByUserIdAndEndDateGreaterThan(eq(USER_ID), any(LocalDateTime.class))).thenReturn(false);
        when(paymentServiceClient.pay(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<>(paymentFailedResponse, HttpStatus.OK));

        CheckException exception = assertThrows(CheckException.class,
                () -> premiumServiceImpl.buyPremium(USER_ID, PAYMENT_NUMBER, period));

        verify(userRepositoryAdapter, times(1)).getUserById(USER_ID);
        verify(premiumRepository, times(1)).existsByUserIdAndEndDateGreaterThan(eq(USER_ID), any(LocalDateTime.class));
        verify(paymentServiceClient, times(1)).pay(any(PaymentRequest.class));
        assertEquals("Оплата не прошла!Повторите попытку!", exception.getMessage());
    }

    @Test
    public void testRemoveExpiredPremiumAccess_successful() {
        doNothing().when(premiumRepository).deleteAll(testExpiredPremiums);

        CompletableFuture<Void> result = premiumServiceImpl.removeExpiredPremiumAccess(testExpiredPremiums);

        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isDone(), "Результат должен быть завершен");
        assertFalse(result.isCompletedExceptionally(), "Результат не должен завершиться с ошибкой");
        verify(premiumRepository, times(1)).deleteAll(testExpiredPremiums);
        assertDoesNotThrow(() -> result.get(), "Получение результата не должно бросать исключение");
    }

    @Test
    void testRemoveExpiredPremiumAccess_WhenRepositoryThrowsDataAccessException_ShouldPropagateException() {
        String errorMessage = "Database connection failed";
        DataAccessException expectedException = new DataAccessException(errorMessage) {
        };
        doThrow(expectedException).when(premiumRepository).deleteAll(testExpiredPremiums);

        DataAccessException thrownException = assertThrows(
                DataAccessException.class,
                () -> premiumServiceImpl.removeExpiredPremiumAccess(testExpiredPremiums),
                "Должно быть брошено DataAccessException"
        );

        assertEquals(errorMessage, thrownException.getMessage(),
                "Сообщение об ошибке должно совпадать с ожидаемым");
        verify(premiumRepository, times(1)).deleteAll(testExpiredPremiums);
    }

    private Premium createExpiredPremium(Long id, LocalDateTime endDate) {
        Premium premium = new Premium();
        premium.setId(id);
        premium.setEndDate(endDate);
        return premium;
    }
}
