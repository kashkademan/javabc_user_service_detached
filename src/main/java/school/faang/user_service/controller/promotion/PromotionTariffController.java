package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionTariffResponseDto;
import school.faang.user_service.facade.promotion.PromotionTariffFacade;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotion-tariffs")
@RequiredArgsConstructor
@Slf4j
public class PromotionTariffController {
    private final PromotionTariffFacade promotionTariffFacade;

    @GetMapping
    public ResponseEntity<List<PromotionTariffResponseDto>> getAllActivePromotionTariff() {
        log.info("Promotion tariff controller accepted request get all active promotion tariff");

        List<PromotionTariffResponseDto> response = promotionTariffFacade.getAllActivePromotionTariff();
        log.info("Promotion tariff controller return response get all active promotion tariff {}", response);
        return ResponseEntity.ok(response);
    }
}
