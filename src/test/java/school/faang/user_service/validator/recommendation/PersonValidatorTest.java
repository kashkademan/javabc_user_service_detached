package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.validator.Validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PersonValidatorTest {
    private final Validator<RecommendationRequestDto> validator = new PersonValidator();

    @Test
    public void testRequesterIdIsNull() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(null, 10L)));
        assertEquals(PersonValidator.REQUESTER_ID_IS_EMPTY, result.getMessage());
    }

    @Test
    public void testRequesterIdIsNegative() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(-10L, 10L)));
        assertEquals(PersonValidator.REQUESTER_ID_IS_EMPTY, result.getMessage());
    }

    @Test
    public void testRequesterIdIsZero() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(0L, 10L)));
        assertEquals(PersonValidator.REQUESTER_ID_IS_EMPTY, result.getMessage());
    }

    @Test
    public void testReceiverIdIsNull() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(10L, null)));
        assertEquals(PersonValidator.RECEIVER_ID_IS_EMPTY, result.getMessage());
    }

    @Test
    public void testReceiverIdIsNegative() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(10L, -20L)));
        assertEquals(PersonValidator.RECEIVER_ID_IS_EMPTY, result.getMessage());
    }

    @Test
    public void testReceiverIdIsZero() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(10L, 0L)));
        assertEquals(PersonValidator.RECEIVER_ID_IS_EMPTY, result.getMessage());
    }

    @Test
    public void testPersonIsSame() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(getDto(10L, 10L)));
        assertEquals(PersonValidator.SAME_PERSON, result.getMessage());
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