package school.faang.user_service.validator.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.validator.Validator;

@Slf4j
@Component
public class RejectValidator implements Validator<RejectionDto> {
    public static final String MESSAGE_IS_EMPTY = "Reject reason is empty";

    @Override
    public void validate(RejectionDto dto) {
        if (dto.reason() == null || dto.reason().isBlank()) {
            log.error("RecommendationRequestDto: {}, error: {}", dto, MESSAGE_IS_EMPTY);
            throw new IllegalArgumentException(MESSAGE_IS_EMPTY);
        }
    }
}
