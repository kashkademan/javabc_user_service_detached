package school.faang.user_service.service.promotion;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.PromotionConfig;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.promoition.PromotionRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.promotion.validator.PromotionValidator;
import school.faang.user_service.service.redis.PromotionRedisService;

import java.util.List;
import java.util.Objects;

import static school.faang.user_service.entity.promotion.PromotionStatus.ACTIVE;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionService {

    private final UserContext userContext;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PromotionRedisService promotionRedisService;
    private final PromotionConfig promotionConfig;


    public Promotion create(Promotion promotion, PaymentRequest paymentRequest) {

        Long userId = userContext.getUserId();
        userRepository.getByIdOrThrow(userId);

        promotion.setUserId(userId);
        List<Promotion> promotionListByUser = promotionRepository.findPromotionByUserId(userId);
        PromotionValidator.validateExistsByUserStatusPromotion(promotionListByUser, userId);
        System.out.println(promotion.getPromotionStatus());
        ResponseEntity<PaymentResponse> responseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse paymentResponse = responseEntity.getBody();

        PromotionValidator.validatePaymentResponse(paymentResponse);

        Integer numberOfDisplay = promotionConfig.getDisplayForTarif(promotion.getTarif());
        promotion.setNumberOfDisplay(numberOfDisplay);
        promotion.setRemainingDisplay(numberOfDisplay);
        promotion.setPromotionStatus(ACTIVE);
        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("a new one was saved promotion {}", promotion.getId());

        promotionRedisService.savePromotionByUser(savedPromotion, savedPromotion.getUserId());
        log.info("Promotion {} saved to Redis successfully", savedPromotion.getId());
        return promotion;
    }

    @Transactional
    public void updatePromotion(UserDto userDto) {
        List<Promotion> promotions = promotionRepository.findPromotionByUserId(userDto.id());
        promotions.forEach(promotion -> {
            promotion.setUpdateForRedis(true);
        });
        promotionRepository.saveAll(promotions);
    }

    public List<Promotion> getPromotionByUserId() {
        Long userId = userContext.getUserId();
        List<Promotion> result = promotionRepository.findPromotionByUserId(userId);
        if (Objects.isNull(result)) {
            throw new EntityNotFoundException(String
                    .format("The user %s currently has no promotion! Now is the time to register", userId));
        }
        return result;
    }

}
