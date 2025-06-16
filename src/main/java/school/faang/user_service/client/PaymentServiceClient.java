package school.faang.user_service.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @Retryable(retryFor = {FeignException.class}, maxAttempts = 5, backoff = @Backoff(delay = 5000, multiplier = 2))
    @PostMapping("/api/payment")
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto);

}
