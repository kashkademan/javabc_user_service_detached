package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PaymentResponse;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.exception.AlreadyPremiumUserException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.messaging.publishers.PremiumBoughtEventPublisher;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PremiumService;
import school.faang.user_service.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final UserService userService;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final UserMapper userMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumBoughtEventPublisher premiumBoughtEventPublisher;

    @Override
    public PremiumDto buyPremium(long userId, PremiumPeriod period) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new AlreadyPremiumUserException("User with id %d already has premium".formatted(userId));
        }
        UserDto userDto = userService.findUserById(userId);
        PaymentRequest buyPremiumRequest = PaymentRequest.builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(BigDecimal.valueOf(period.getPrice()))
                .currency(Currency.USD)
                .build();
        PaymentResponse paymentResult = paymentServiceClient.sendPayment(buyPremiumRequest);
        if (!paymentResult.status().equals(PaymentStatus.SUCCESS)) {
            throw new PaymentFailedException("Failed to buy premium for %d days by user with id %d"
                    .formatted(period.getDays(), userId));
        }
        Premium boughtPremium = Premium.builder()
                .user(userMapper.toUser(userDto))
                .price(BigDecimal.valueOf(period.getPrice()))
                .currency(Currency.USD)
                .premiumPeriod(period)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
        Premium savedPremium = premiumRepository.save(boughtPremium);
        premiumBoughtEventPublisher.publishMessage(savedPremium);
        return premiumMapper.toPremiumDto(savedPremium);
    }

    @Override
    @Transactional
    public void removePremium(int batchSize) {
        LocalDateTime now = LocalDateTime.now();
        List<Premium> expiredPremium = premiumRepository.findAllByEndDateBefore(now);
        Stream<List<Premium>> stream = IntStream.range(0, (expiredPremium.size() + batchSize - 1 / batchSize))
                .mapToObj(i -> expiredPremium.subList(i * batchSize, Math.min((i + 1) * batchSize ,
                        expiredPremium.size())));
        List<List<Premium>> batches = stream.toList();

        batches.parallelStream()
                .forEach(premiumRepository::deleteAll);
    }
}