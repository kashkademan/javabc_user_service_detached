package school.faang.user_service.service.premium.impl;

import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.adapter.user.UserRepositoryAdapter;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.entity.premium.PremiumPeriod;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.CheckException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.scheduler.premium.PremiumAccessBatch;
import school.faang.user_service.scheduler.premium.PremiumListPartitioner;
import school.faang.user_service.service.premium.PremiumService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private static final String NO_EXPIRED_PREMIUM_FOUND = "There is no expired premium subscription found";
    private static final String START_REMOVING = "Start to remove all expired premium accesses";
    private static final String INTEGRATION_ERR_MSG = "Ошибка взаимодействия с сервисом оплат!";
    private static final String SUCCESS_REMOVING_WITH_BATCHES = "Successfully removed all expired premium accesses from {} batches";
    private static final String ERROR_DURING_PARALLEL_REMOVAL = "Error during parallel premium removal. Some batches may have failed.";
    private static final String REMOVAL_COMPLETED_WITH_ERRORS = "Premium removal completed with errors. Check debug logs for batch details.";
    private static final String BATCH_REMOVED_SUCCESSFULLY = "Successfully removed {} premium accesses";
    private static final String BATCH_REMOVAL_ERROR = "Error removing premium accesses";

    private final UserRepositoryAdapter userRepositoryAdapter;
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;
    private final PremiumListPartitioner listPartitioner;

    @Value("${premium.scheduler.batch-size}")
    private int batchSize;

    @Override
    @Transactional
    public PremiumDto buyPremium(long userid, long paymentNumber, PremiumPeriod premiumPeriod) {
        User user = userRepositoryAdapter.getUserById(userid);

        if (premiumRepository.existsByUserIdAndEndDateGreaterThan(userid, LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    String.format("У пользователя с id: %s уже есть премиум-доступ", userid));
        }

        PaymentResponse paymentResponse = sendPayment(premiumPeriod.getPrice(), Currency.USD, paymentNumber);
        if (paymentResponse.getStatus() != PaymentStatus.SUCCESS) {
            throw new CheckException("Оплата не прошла!Повторите попытку!");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        return premiumMapper.toDto(
                premiumRepository.save(Premium.builder()
                        .user(user)
                        .startDate(currentDateTime)
                        .endDate(currentDateTime.plusMonths(premiumPeriod.getMonths()))
                        .build()));
    }

    private PaymentResponse sendPayment(@NotNull BigDecimal amount, @NotNull Currency currency, long paymentNumber) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Amount не может быть 0");
        }
        if (!Arrays.asList(Currency.values()).contains(currency)) {
            throw new IllegalArgumentException("Неверный параметр currency");
        }
        try {
            ResponseEntity<PaymentResponse> responseEntity = paymentServiceClient.pay(
                    new PaymentRequest(paymentNumber, amount, currency));
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.debug("paymentResponse response:{} {}", responseEntity.getStatusCode(), responseEntity.getBody());
                return responseEntity.getBody();
            } else {
                log.warn("paymentResponse response:{} {}", responseEntity.getStatusCode(), responseEntity.getBody());
                throw new IllegalArgumentException(responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR ? INTEGRATION_ERR_MSG : String.valueOf(responseEntity.getBody()));
            }
        } catch (FeignException e) {
            log.error("paymentResponse response:{}", e.toString());
            throw new IllegalArgumentException(INTEGRATION_ERR_MSG);
        }
    }

    @Override
    public void removeAllExpiredPremiumAccesses() {
        List<Premium> premiumAccesses = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (premiumAccesses.isEmpty()) {
            log.info(NO_EXPIRED_PREMIUM_FOUND);
            return;
        }

        log.info(START_REMOVING);
        List<PremiumAccessBatch> batches = listPartitioner.partition(premiumAccesses, batchSize);

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> removeExpiredPremiumBatch(batch.getPremiums()))
                .toList();

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info(SUCCESS_REMOVING_WITH_BATCHES, batches.size());
        } catch (Exception e) {
            log.error(ERROR_DURING_PARALLEL_REMOVAL, e);
            log.warn(REMOVAL_COMPLETED_WITH_ERRORS);
        }
    }

    @Override
    @Async("fixedThreadPool")
    @Transactional
    public CompletableFuture<Void> removeExpiredPremiumBatch(List<Premium> premiums) {
        try {
            premiumRepository.deleteAll(premiums);
            log.debug(BATCH_REMOVED_SUCCESSFULLY, premiums.size());
            return null;
        } catch (Exception e) {
            log.error(BATCH_REMOVAL_ERROR, e);
            throw e;
        }
    }
}