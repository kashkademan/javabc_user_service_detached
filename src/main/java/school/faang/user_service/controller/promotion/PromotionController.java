package school.faang.user_service.controller.promotion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.facade.promotion.PromotionFacade;
import school.faang.user_service.dto.promotion.PromotionCreateDto;
import school.faang.user_service.dto.promotion.PromotionDto;

@RequiredArgsConstructor
@RequestMapping("/api/v1/promotions")
@RestController
public class PromotionController {

    private final PromotionFacade promotionFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionDto createPromotion(@Valid @RequestBody PromotionCreateDto promotionCreateDto) {
        return promotionFacade.createPromotion(promotionCreateDto);
    }
}
