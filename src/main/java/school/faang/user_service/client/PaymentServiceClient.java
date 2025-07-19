package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;

/**
 * Клиент для взаимодействия с платёжным микросервисом (payment-service) через Feign.
 * <p>
 * Предоставляет метод для отправки запроса на проведение платежа и получения результата.
 * Используется в сервисах для выполнения межсервисных вызовов к payment-service.
 * </p>
 *
 * Вызов метода sendPayment отправляет POST-запрос на эндпоинт /payment платёжного сервиса с данными платежа.
 * Ответом является объект {@link PaymentResponse}, содержащий статус и детали операции.
 * Конфигурация клиента определяется классом {@link FeignConfig}, который, например, добавляет HTTP-заголовки.
 *
 * @author agent
 * @since 10.07.2025
 */
@FeignClient(
        name = "payment-service",
        url = "${payment-service.host}",
        configuration = FeignConfig.class
)
public interface PaymentServiceClient {

    @PostMapping("/payment")
    PaymentResponse sendPayment(@RequestBody PaymentRequest request);
}