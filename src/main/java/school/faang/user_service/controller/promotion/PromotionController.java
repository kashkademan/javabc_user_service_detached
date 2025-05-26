package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionCreateRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.facade.promotion.PromotionFacade;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Slf4j
public class PromotionController {
    private final PromotionFacade promotionFacade;

    @PostMapping
    public ResponseEntity<PromotionResponseDto> createPromotion
            (@RequestBody PromotionCreateRequestDto promotionCreateRequestDto) {
        log.info("Promotion controller accepted request get create promotion {}", promotionCreateRequestDto);

        PromotionResponseDto response = promotionFacade.createPromotion(promotionCreateRequestDto);
        log.info("Promotion  controller return response get create promotion {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
