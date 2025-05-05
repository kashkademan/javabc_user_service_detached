package school.faang.user_service.validator.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.validator.Validator;

@Slf4j
@Component
public class MessageValidator implements Validator<RecommendationRequestDto> {
    public static final String MESSAGE_IS_EMPTY = "recommendation message is empty";

    @Override
    public void validate(RecommendationRequestDto dto) {
        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            log.error("RecommendationRequestDto: {}, error: {}", dto, MESSAGE_IS_EMPTY);
            throw new IllegalArgumentException(MESSAGE_IS_EMPTY);
        }
    }
}
