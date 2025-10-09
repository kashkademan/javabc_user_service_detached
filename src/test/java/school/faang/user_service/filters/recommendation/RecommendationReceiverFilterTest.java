package school.faang.user_service.filters.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RecommendationReceiverFilterTest {
    private final RecommendationReceiverFilter filter = new RecommendationReceiverFilter();

    @Test
    public void testIsApplicable_withNonNullReceiverId_returnsTrue() {
        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                null, null, 123L
        );
        assertTrue(filter.isApplicable(filterDto));
        filterDto = new FilterRecommendationRequestDto(
                null, null, null
        );
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable_withNullReceiverId_returnsFalse() {
        FilterRecommendationRequestDto filterDto = null;
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    public void testApply_withMatchingReceiverId_filtersRecommendationsCorrectly() {
        User author = User.builder().id(123L).build();
        User receiver1 = User.builder().id(456L).build();
        User receiver2 = User.builder().id(789L).build();

        Recommendation rec1 = Recommendation.builder().id(1L).content("Recommendation 1")
                .author(author).receiver(receiver1).build();
        Recommendation rec2 = Recommendation.builder().id(2L).content("Recommendation 2")
                .author(author).receiver(receiver2).build();
        Recommendation rec3 = Recommendation.builder().id(3L).content("Recommendation 3")
                .author(author).receiver(receiver2).build();

        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                null, null, 456L
        );

        Stream<Recommendation> filteredStream = filter.apply(Stream.of(rec1, rec2, rec3), filterDto);
        List<Recommendation> filteredRecommendations = filteredStream.toList();

        assertEquals(1, filteredRecommendations.size());
        assertEquals(456L, filteredRecommendations.get(0).getReceiver().getId());
    }

    @Test
    public void testApply_withNonMatchingReceiverId_excludesAllRecommendations() {
        User author = User.builder().id(123L).build();
        User receiver1 = User.builder().id(456L).build();
        User receiver2 = User.builder().id(789L).build();

        Recommendation rec1 = Recommendation.builder().id(1L).content("Recommendation 1")
                .author(author).receiver(receiver1).build();
        Recommendation rec2 = Recommendation.builder().id(2L).content("Recommendation 2")
                .author(author).receiver(receiver2).build();
        Recommendation rec3 = Recommendation.builder().id(3L).content("Recommendation 3")
                .author(author).receiver(receiver2).build();

        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                null, null, 999L
        );

        Stream<Recommendation> filteredStream = filter.apply(Stream.of(rec1, rec2, rec3), filterDto);
        List<Recommendation> filteredRecommendations = filteredStream.toList();

        assertTrue(filteredRecommendations.isEmpty());
    }

    @Test
    public void testApply_withNullReceiverId_doesNotFilterRecommendations() {
        User author = User.builder().id(123L).build();
        User receiver1 = User.builder().id(456L).build();
        User receiver2 = User.builder().id(789L).build();

        Recommendation rec1 = Recommendation.builder().id(1L).content("Recommendation 1")
                .author(author).receiver(receiver1).build();
        Recommendation rec2 = Recommendation.builder().id(2L).content("Recommendation 2")
                .author(author).receiver(receiver2).build();
        Recommendation rec3 = Recommendation.builder().id(3L).content("Recommendation 3")
                .author(author).receiver(receiver2).build();

        FilterRecommendationRequestDto filterDto = new FilterRecommendationRequestDto(
                null, null, null
        );

        Stream<Recommendation> filteredStream = filter.apply(Stream.of(rec1, rec2, rec3), filterDto);
        List<Recommendation> filteredRecommendations = filteredStream.toList();

        assertEquals(3, filteredRecommendations.size());
    }
}