package school.faang.user_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;

import static school.faang.user_service.dto.payment.PaymentStatus.SUCCESS;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    //@PostMapping("/api/payment") url - куда отправляем запрос на оплату
    default ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest paymentRequest) {
        //вроде как замокал
        PaymentResponse paymentResponse = new PaymentResponse(SUCCESS, 0, 0,
                null, null, null);

        return ResponseEntity.ok(paymentResponse);
    }
}
