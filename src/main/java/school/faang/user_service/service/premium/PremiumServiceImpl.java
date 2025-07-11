package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.UserWithPremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.exception.PremiumAlreadyExistsException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;

    @Transactional
    @Override
    public PremiumDto buyPremium(Long userId, PremiumPeriod period) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new PremiumAlreadyExistsException("User already has premium access");
        }

        PaymentRequest request = new PaymentRequest(
                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                BigDecimal.valueOf(period.getPrice()),
                Currency.USD
        );

        PaymentResponse response = paymentServiceClient.sendPayment(request);

        if (response.status() != PaymentStatus.SUCCESS) {
            throw new PaymentFailedException("Payment was not successful");
        }

        User user = new User();
        user.setId(userId);

        Premium premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();

        premiumRepository.save(premium);

        return premiumMapper.toDto(premium);
    }

    @Override
    @Transactional
    public List<UserWithPremiumDto> getUsersWithActivePremium() {
        LocalDateTime now = LocalDateTime.now();
        List<Premium> activePremiums = premiumRepository.findAllByEndDateAfter(now);

        return activePremiums.stream()
                .map(premiumMapper::toUserWithRemainingDays)
                .toList();
    }
}