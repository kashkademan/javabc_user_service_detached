package school.faang.user_service.service.payment;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentClientRequestDto;
import school.faang.user_service.entity.promotion.PromotionTariff;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.entity.promotion.Currency.RUB;
import static school.faang.user_service.entity.promotion.Currency.USD;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @InjectMocks
    private PaymentService paymentService;
    @Captor
    private ArgumentCaptor<PaymentClientRequestDto> paymentCaptor;

    @Test
    void testSendPayment_success() {
        PromotionTariff tariff = new PromotionTariff();
        tariff.setId(1L);
        tariff.setPrice(BigDecimal.valueOf(100));
        tariff.setCurrency(RUB);

        assertDoesNotThrow(() -> paymentService.sendPayment(tariff));

        verify(paymentServiceClient).sendPayment(paymentCaptor.capture());

        PaymentClientRequestDto sent = paymentCaptor.getValue();
        assertNotNull(sent);
        assertEquals(tariff.getPrice(), sent.getAmount());
        assertEquals(tariff.getCurrency(), sent.getCurrency());
    }

    @Test
    void testSendPayment_feignException() {
        PromotionTariff tariff = new PromotionTariff();
        tariff.setId(2L);
        tariff.setPrice(BigDecimal.valueOf(200));
        tariff.setCurrency(USD);

        doThrow(FeignException.ServiceUnavailable.class)
                .when(paymentServiceClient)
                .sendPayment(any());

        assertThrows(FeignException.class, () -> paymentService.sendPayment(tariff));
        verify(paymentServiceClient).sendPayment(any(PaymentClientRequestDto.class));
    }
}