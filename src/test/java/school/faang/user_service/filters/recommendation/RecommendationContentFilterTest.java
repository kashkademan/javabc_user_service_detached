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

public class RecommendationContentFilterTest {
    private final RecommendationContentFilter filter = new RecommendationContentFilter();

    @Test
    public void testIsApplicable_withNonEmptyContent_returnsTrue() {
        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                "test", null, null
        );
        boolean result = filter.isApplicable(filterDto);
        assertTrue(result);

        filterDto = new FilterRecommendationRequestDto(null, null, null);
        assertTrue(filter.isApplicable(filterDto));
        filterDto = new FilterRecommendationRequestDto(
                "   ", null, null
        );
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable_withNullContent_returnsFalse() {
        FilterRecommendationRequestDto filterDto = null;
        assertFalse(filter.isApplicable(filterDto));
    }


    @Test
    public void testApply_withMatchingContent_filtersRecommendationsCorrectly() {
        User author = User.builder().id(123L).build();
        User receiver = User.builder().id(456L).build();

        Recommendation rec1 = Recommendation.builder()
                .content("Good Java Developer").author(author).receiver(receiver).build();
        Recommendation rec2 = Recommendation.builder()
                .content("Excellent Python Developer").author(author).receiver(receiver).build();
        Recommendation rec3 = Recommendation.builder()
                .content("Good JavaScript Developer").author(author).receiver(receiver).build();

        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                "Java", null, null
        );

        Stream<Recommendation> filteredStream = filter.apply(Stream.of(rec1, rec2, rec3), filterDto);
        List<Recommendation> filteredRecommendations = filteredStream.toList();

        assertTrue(filteredRecommendations.get(0).getContent().contains("Java"));
        assertTrue(filteredRecommendations.get(1).getContent().contains("Java"));
        assertEquals(2, filteredRecommendations.size());
    }

    @Test
    public void testApply_withCaseInsensitiveContent_filtersCorrectly() {
        User author = User.builder().id(123L).build();
        User receiver = User.builder().id(456L).build();

        Recommendation rec1 = Recommendation.builder()
                .content("Good JAVA Developer").author(author).receiver(receiver).build();
        Recommendation rec2 = Recommendation.builder()
                .content("Good java Developer").author(author).receiver(receiver).build();

        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                "JAva", null, null
        );

        Stream<Recommendation> filteredStream = filter.apply(Stream.of(rec1, rec2), filterDto);
        List<Recommendation> filteredRecommendations = filteredStream.toList();

        assertEquals(2, filteredRecommendations.size());
    }

    @Test
    public void testApply_withNullOrBlankContent_returnsAllRecommendations() {
        User author = User.builder().id(123L).build();
        User receiver = User.builder().id(456L).build();

        Recommendation rec1 = Recommendation.builder()
                .content("Good Java Developer").author(author).receiver(receiver).build();
        Recommendation rec2 = Recommendation.builder()
                .content("Good Python Developer").author(author).receiver(receiver).build();

        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                null, null, null
        );

        Stream<Recommendation> filteredStream = filter.apply(Stream.of(rec1, rec2), filterDto);
        List<Recommendation> filteredRecommendations = filteredStream.toList();

        assertEquals(2, filteredRecommendations.size());
    }
}