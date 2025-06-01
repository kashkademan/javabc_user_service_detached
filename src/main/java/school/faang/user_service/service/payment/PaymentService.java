package school.faang.user_service.service.payment;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentClientRequestDto;
import school.faang.user_service.entity.promotion.PromotionTariff;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentServiceClient paymentServiceClient;

    @Retryable(retryFor = {FeignException.class, RetryableException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendPayment(PromotionTariff tariff) {
        PaymentClientRequestDto paymentClientRequestDto = new PaymentClientRequestDto();
        paymentClientRequestDto.setPaymentNumber(generatePaymentNumber(tariff.getId()));
        paymentClientRequestDto.setAmount(tariff.getPrice());
        paymentClientRequestDto.setCurrency(tariff.getCurrency());

        paymentServiceClient.sendPayment(paymentClientRequestDto);
    }

    private long generatePaymentNumber(long promotionTariffId) {
        return Long.parseLong(String.valueOf(promotionTariffId) + System.currentTimeMillis() % 1_000_000);
    }
}
