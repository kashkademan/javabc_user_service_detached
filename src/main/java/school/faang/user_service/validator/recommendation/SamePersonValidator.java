package school.faang.user_service.validator.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.validator.Validator;

@Slf4j
@Component
public class SamePersonValidator implements Validator<RecommendationRequestDto> {
    public static final String SAME_PERSON = "Requester and Receiver is a same person";
    public static final String REQUESTER_ID_IS_EMPTY = "recommendation requester id is empty or 0";
    public static final String RECEIVER_ID_IS_EMPTY = "recommendation receiver id is empty or 0";

    @Override
    public void validate(RecommendationRequestDto dto) {
        if (dto.getRequesterId() == null || dto.getRequesterId().compareTo(1L) < 0) {
            log.error("RecommendationRequestDto: {}, error: {}", dto, REQUESTER_ID_IS_EMPTY);
            throw new IllegalArgumentException(REQUESTER_ID_IS_EMPTY);
        }

        if (dto.getReceiverId() == null || dto.getReceiverId().compareTo(1L) < 0) {
            log.error("RecommendationRequestDto: {}, error: {}", dto, RECEIVER_ID_IS_EMPTY);
            throw new IllegalArgumentException(RECEIVER_ID_IS_EMPTY);
        }

        if (dto.getRequesterId().equals(dto.getReceiverId())) {
            log.error("RecommendationRequestDto: {}, error: {}", dto, SAME_PERSON);
            throw new IllegalArgumentException(SAME_PERSON);
        }
    }
}
