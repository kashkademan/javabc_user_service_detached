package school.faang.user_service.dto.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationRequestDtoTest {

    @Test
    @DisplayName("Test adding IDs into RecommendationRequestDto skills")
    void testAddSkill() {
        RecommendationRequestDto dto = RecommendationRequestDto.builder()
                .id(1L)
                .build();
        dto.addSkill(10L);
        dto.addSkill(20L);

        assertNotNull(dto.getSkills());
        assertEquals(2, dto.getSkills().size());
    }
}