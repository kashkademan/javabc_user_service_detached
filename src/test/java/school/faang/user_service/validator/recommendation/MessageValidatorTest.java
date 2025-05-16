package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.validator.Validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageValidatorTest {
    private final Validator<RecommendationRequestDto> validator = new MessageValidator();

    @Test
    public void testValidatorWithNullMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(null)));
    }

    @Test
    public void testValidatorWithBlankMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto("   ")));
    }

    @Test
    public void testValidatorWithEmptyMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto("")));
    }

    @Test
    public void testValidatorWithFillingMessage() {
        assertDoesNotThrow(() -> validator.validate(getDto("message")));
    }

    private RecommendationRequestDto getDto(String message) {
        return RecommendationRequestDto.builder()
                .message(message)
                .build();
    }
}