package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.validator.Validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SamePersonValidatorTest {
    private final Validator<RecommendationRequestDto> validator = new SamePersonValidator();

    @Test
    public void testRequesterIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(null, 10L)));
    }

    @Test
    public void testRequesterIdIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(-10L, 10L)));
    }

    @Test
    public void testRequesterIdIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(0L, 10L)));
    }

    @Test
    public void testReceiverIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(10L, null)));
    }

    @Test
    public void testReceiverIdIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(10L, -20L)));
    }

    @Test
    public void testReceiverIdIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(10L, 0L)));
    }

    @Test
    public void testPersonIsSame() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(10L, 10L)));
    }

    @Test
    public void testPersonIsNotSame() {
        assertDoesNotThrow(() -> validator.validate(getDto(10L, 11L)));
    }

    private RecommendationRequestDto getDto(Long requesterId, Long receiverId) {
        return RecommendationRequestDto.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();
    }
}