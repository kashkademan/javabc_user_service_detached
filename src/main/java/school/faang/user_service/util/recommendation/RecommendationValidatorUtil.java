package school.faang.user_service.util.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;

public class RecommendationValidatorUtil {
   public static void validate(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new DataValidationException("Рекомендация не может быть null");
        }
        if (recommendationDto.getAuthorId() == null) {
            throw new DataValidationException("ID автора не может быть null");
        }
        if (recommendationDto.getReceiverId() == null) {
            throw new DataValidationException("ID получателя не может быть null");
        }
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Текст рекомендации не может быть пустым");
        }
    }
}