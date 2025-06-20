package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.payment.enums.Currency;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.common.PreConditionFailedException;
import school.faang.user_service.exception.payment.PaymentException;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.util.enums.PremiumPeriod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static school.faang.user_service.util.LogsConstants.PAYMENT_PROBLEMS;
import static school.faang.user_service.util.LogsConstants.USER_HAS_PREMIUM;

@RequiredArgsConstructor
@Slf4j
@Service
public class PremiumService {

    private final UserService userService;
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;

    public Premium buyPremium(int days) {
        User user = userService.getCurrentUser();
        validateUserHasPremium(user);

        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        PaymentRequest paymentRequest = buildUSDPaymentRequest(premiumPeriod);
        ResponseEntity<PaymentResponse> response = paymentServiceClient.sendPayment(paymentRequest);
        validateResponseStatus(response);

        Premium premium = buildPremium(user, premiumPeriod.getDaysAmount());
        premiumRepository.save(premium);
        return premium;
    }

    private void validateUserHasPremium(User user) {
        boolean userHasPremium = premiumRepository.existsByUserId(user.getId());
        if (userHasPremium) {
            log.error(USER_HAS_PREMIUM);
            throw new PreConditionFailedException(USER_HAS_PREMIUM);
        }
    }

    private PaymentRequest buildUSDPaymentRequest(PremiumPeriod premiumPeriod) {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(new BigDecimal(premiumPeriod.getPrice()))
                .fromCurrency(Currency.USD)
                .toCurrency(Currency.USD)
                .build();
        log.info("buildUSDPaymentRequest paymentRequest = {}", paymentRequest);
        return paymentRequest;
    }

    private Premium buildPremium(User user, int days) {
        LocalDateTime now = LocalDateTime.now();
        return Premium.builder()
                .user(user)
                .startDate(now)
                .endDate(now.plusDays(days))
                .build();
    }

    private void validateResponseStatus(ResponseEntity<PaymentResponse> response) {
        log.info("validateResponseStatus response : {}", response);
        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            log.error(PAYMENT_PROBLEMS);
            throw new PaymentException(PAYMENT_PROBLEMS);
        } else {
            PaymentResponse paymentResponse = response.getBody();
            log.info("validateResponseStatus paymentResponse : {}", paymentResponse);
            if (paymentResponse == null || !Objects.equals(paymentResponse.status(), PaymentStatus.SUCCESS)) {
                log.error(PAYMENT_PROBLEMS);
                throw new PaymentException(PAYMENT_PROBLEMS);
            }
        }
    }
}
