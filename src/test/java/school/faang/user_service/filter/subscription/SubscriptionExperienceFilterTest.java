package school.faang.user_service.filter.subscription;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscription.SubscriptionFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubscriptionExperienceFilterTest {
    private final SubscriptionExperienceFilter filters = new SubscriptionExperienceFilter();

    @Test
    public void testApplicableTrueForMinValue() {
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);
        when(dtoFilter.getExperienceMin()).thenReturn(1);
        when(dtoFilter.getExperienceMax()).thenReturn(0);

        boolean result = filters.isApplicable(dtoFilter);
        assertTrue(result);
    }

    @Test
    public void testApplicableTrueForMaxValue() {
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);
        when(dtoFilter.getExperienceMin()).thenReturn(0);
        when(dtoFilter.getExperienceMax()).thenReturn(1);

        boolean result = filters.isApplicable(dtoFilter);
        assertTrue(result);
    }

    @Test
    public void testApplicableTrueForBothValues() {
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);
        when(dtoFilter.getExperienceMin()).thenReturn(1);
        when(dtoFilter.getExperienceMax()).thenReturn(1);

        boolean result = filters.isApplicable(dtoFilter);
        assertTrue(result);
    }

    @Test
    public void testApplicableFalse() {
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);
        when(dtoFilter.getExperienceMin()).thenReturn(0);
        when(dtoFilter.getExperienceMax()).thenReturn(0);

        boolean result = filters.isApplicable(dtoFilter);
        assertFalse(result);
    }

    @Test
    public void testApplyEntityFieldIsNull() {
        User user = mock(User.class);
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);

        when(user.getExperience()).thenReturn(null);
        when(dtoFilter.getExperienceMin()).thenReturn(1);
        when(dtoFilter.getExperienceMax()).thenReturn(1);

        boolean result = filters.apply(user, dtoFilter);
        assertFalse(result);
    }

    @Test
    public void testApplyEntityFieldBetweenFilterValues() {
        User user = mock(User.class);
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);

        when(user.getExperience()).thenReturn(20);
        when(dtoFilter.getExperienceMin()).thenReturn(1);
        when(dtoFilter.getExperienceMax()).thenReturn(20);

        boolean result = filters.apply(user, dtoFilter);
        assertTrue(result);
    }

    @Test
    public void testApplyEntityFieldNotBetweenFilterValues() {
        User user = mock(User.class);
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);

        when(user.getExperience()).thenReturn(0);
        when(dtoFilter.getExperienceMin()).thenReturn(1);
        when(dtoFilter.getExperienceMax()).thenReturn(20);

        boolean result = filters.apply(user, dtoFilter);
        assertFalse(result);
    }
}

