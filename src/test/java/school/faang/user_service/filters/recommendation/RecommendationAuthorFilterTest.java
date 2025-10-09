package school.faang.user_service.filters.recommendation;

import org.junit.jupiter.api.Test;

import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendationAuthorFilterTest {
    private final RecommendationAuthorFilter filter = new RecommendationAuthorFilter();

    @Test
    public void testIsApplicable_withNonNullAuthorId_returnsTrue() {
        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                null, null, null
        );
        boolean result = filter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_withNullAuthorId_returnsFalse() {
        FilterRecommendationRequestDto filterDto = null;
        boolean result = filter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    public void testApply_withMatchingAuthorId_filtersRecommendationsCorrectly() {
        User author1 = User.builder().id(123L).build();
        User author2 = User.builder().id(456L).build();
        User receiver = User.builder().id(789L).build();

        Recommendation rec1 = Recommendation.builder().id(1L).content("Recommendation 1")
                .author(author1).receiver(receiver).build();
        Recommendation rec2 = Recommendation.builder().id(2L).content("Recommendation 2")
                .author(author2).receiver(receiver).build();
        Recommendation rec3 = Recommendation.builder().id(3L).content("Recommendation 3")
                .author(author2).receiver(receiver).build();

        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                "Recommendation 1", 123L, null
        );

        Stream<Recommendation> filteredStream = filter.apply(Stream.of(rec1, rec2, rec3), filterDto);
        List<Recommendation> filteredRecommendations = filteredStream.toList();

        assertEquals(1, filteredRecommendations.size());
        assertEquals(123L, filteredRecommendations.get(0).getAuthor().getId());
    }

    @Test
    public void testApply_withNonMatchingAuthorId_excludesAllRecommendations() {
        User author1 = User.builder().id(123L).build();
        User author2 = User.builder().id(456L).build();
        User receiver = User.builder().id(789L).build();

        Recommendation rec1 = Recommendation.builder().id(1L).content("Recommendation 1")
                .author(author1).receiver(receiver).build();
        Recommendation rec2 = Recommendation.builder().id(2L).content("Recommendation 2")
                .author(author2).receiver(receiver).build();
        Recommendation rec3 = Recommendation.builder().id(3L).content("Recommendation 3")
                .author(author2).receiver(receiver).build();
        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                null, 999L, null
        );

        Stream<Recommendation> filteredStream = filter.apply(Stream.of(rec1, rec2, rec3), filterDto);
        List<Recommendation> filteredRecommendations = filteredStream.toList();

        assertTrue(filteredRecommendations.isEmpty());
    }

    @Test
    public void testApply_withNullAuthorId_doesNotFilterRecommendations() {
        User author1 = User.builder().id(123L).build();
        User author2 = User.builder().id(456L).build();
        User receiver = User.builder().id(789L).build();

        Recommendation rec1 = Recommendation.builder().id(1L).content("Recommendation 1")
                .author(author1).receiver(receiver).build();
        Recommendation rec2 = Recommendation.builder().id(2L).content("Recommendation 2")
                .author(author2).receiver(receiver).build();
        Recommendation rec3 = Recommendation.builder().id(3L).content("Recommendation 3")
                .author(author2).receiver(receiver).build();

        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                null, null, null
        );

        Stream<Recommendation> filteredStream = filter.apply(Stream.of(rec1, rec2, rec3), filterDto);
        List<Recommendation> filteredRecommendations = filteredStream.toList();

        assertEquals(3, filteredRecommendations.size());
    }
}
