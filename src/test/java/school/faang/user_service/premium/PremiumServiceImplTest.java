package school.faang.user_service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.PremiumAlreadyExistsException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.premium.PremiumServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для сервиса PremiumServiceImpl")
@ExtendWith(MockitoExtension.class)
class PremiumServiceImplTest {

    @InjectMocks
    private PremiumServiceImpl premiumService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumMapper premiumMapper;

    @Test
    @DisplayName("Покупка премиума проходит успешно при валидных условиях")
    void buyPremium_shouldSucceed_whenValidConditions() {
        long userId = 42L;
        PremiumPeriod period = PremiumPeriod.ONE_MONTH;

        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);

        PaymentResponse response = new PaymentResponse(
                PaymentStatus.SUCCESS,
                123456,
                999999L,
                BigDecimal.valueOf(10),
                Currency.USD,
                "Payment completed successfully"
        );

        when(paymentServiceClient.sendPayment(any())).thenReturn(response);

        Premium premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();

        when(premiumRepository.save(any())).thenReturn(premium);

        PremiumDto dto = PremiumDto.builder()
                .userId(userId)
                .startDate(premium.getStartDate().toLocalDate())
                .endDate(premium.getEndDate().toLocalDate())
                .build();

        when(premiumMapper.toDto(any())).thenReturn(dto);

        PremiumDto result = premiumService.buyPremium(userId, period);

        assertEquals(userId, result.getUserId());
        assertEquals(dto.getStartDate(), result.getStartDate());
        verify(premiumRepository).save(any());
    }

    @Test
    @DisplayName("Покупка премиума выбрасывает исключение, если премиум уже есть у пользователя")
    void buyPremium_shouldThrow_whenAlreadyHasPremium() {
        when(premiumRepository.existsByUserId(1L)).thenReturn(true);

        assertThrows(PremiumAlreadyExistsException.class,
                () -> premiumService.buyPremium(1L, PremiumPeriod.ONE_MONTH));
    }
}
