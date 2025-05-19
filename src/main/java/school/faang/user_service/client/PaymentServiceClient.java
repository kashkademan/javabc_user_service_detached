package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;

@FeignClient(
        name = "paymentClient",
        url = "${service.payment.url}",
        configuration = FeignConfig.class
)
public interface PaymentServiceClient {

    @PostMapping("/payment")
    PaymentResponseDto buyPremium(@RequestBody PaymentRequestDto request);
}
