package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.payment.PaymentClientRequestDto;
import school.faang.user_service.dto.payment.PaymentClientResponseDto;

@FeignClient(name = "payment-service",
        url = "${payment-service.host}:${payment-service.port}",
        path = "/api/v1/payment",
        configuration = FeignConfig.class)
public interface PaymentServiceClient {
    @PostMapping
    PaymentClientResponseDto sendPayment(@RequestBody PaymentClientRequestDto paymentClientRequestDto);
}
