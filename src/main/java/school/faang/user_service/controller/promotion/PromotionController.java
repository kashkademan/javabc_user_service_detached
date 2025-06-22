package school.faang.user_service.controller.promotion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionEventCreateRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.dto.promotion.PromotionUserCreateRequestDto;
import school.faang.user_service.facade.promotion.PromotionFacade;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Slf4j
public class PromotionController {
    private final PromotionFacade promotionFacade;

    @PostMapping("/event")
    public ResponseEntity<PromotionResponseDto> createPromotionForEvent
            (@RequestBody @Valid PromotionEventCreateRequestDto promotionEventCreateRequestDto) {
        log.info("Promotion controller accepted request create promotion for event {}", promotionEventCreateRequestDto);

        PromotionResponseDto response = promotionFacade.createPromotionForEvent(promotionEventCreateRequestDto);
        log.info("Promotion  controller return response create promotion for event {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/user")
    public ResponseEntity<PromotionResponseDto> createPromotionForUser
            (@RequestBody @Valid PromotionUserCreateRequestDto promotionUserCreateRequestDto) {
        log.info("Promotion controller accepted request create promotion for user {}", promotionUserCreateRequestDto);

        PromotionResponseDto response = promotionFacade.createPromotionForUser(promotionUserCreateRequestDto);
        log.info("Promotion  controller return response create promotion for user {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
