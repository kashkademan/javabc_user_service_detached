package school.faang.user_service.filter.subscription;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscription.SubscriptionFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestSubscriptionPhoneFilters {
    private final SubscriptionPhoneFilters filters = new SubscriptionPhoneFilters();

    @Test
    public void testApplicableTrue() {
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);
        when(dtoFilter.getPhonePattern()).thenReturn("111");

        boolean result = filters.isApplicable(dtoFilter);
        assertTrue(result);
    }

    @Test
    public void testApplicableEmpty() {
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);
        when(dtoFilter.getPhonePattern()).thenReturn("");

        boolean result = filters.isApplicable(dtoFilter);
        assertFalse(result);
    }

    @Test
    public void testApplicableBlank() {
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);
        when(dtoFilter.getPhonePattern()).thenReturn("   ");

        boolean result = filters.isApplicable(dtoFilter);
        assertFalse(result);
    }

    @Test
    public void testApplicableNull() {
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);
        when(dtoFilter.getPhonePattern()).thenReturn(null);

        boolean result = filters.isApplicable(dtoFilter);
        assertFalse(result);
    }

    @Test
    public void testApplyEntityFieldIsNull() {
        User user = mock(User.class);
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);

        when(user.getPhone()).thenReturn(null);
        when(dtoFilter.getPhonePattern()).thenReturn("111");

        boolean result = filters.apply(user, dtoFilter);
        assertFalse(result);
    }

    @Test
    public void testApplyEntityFieldIsEmpty() {
        User user = mock(User.class);
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);

        when(user.getPhone()).thenReturn("");
        when(dtoFilter.getPhonePattern()).thenReturn("111");

        boolean result = filters.apply(user, dtoFilter);
        assertFalse(result);
    }

    @Test
    public void testApplyEntityFieldDoesNotContainPattern() {
        User user = mock(User.class);
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);

        when(user.getPhone()).thenReturn("222");
        when(dtoFilter.getPhonePattern()).thenReturn("111");

        boolean result = filters.apply(user, dtoFilter);
        assertFalse(result);
    }

    @Test
    public void testApplyEntityFieldContainsPattern() {
        User user = mock(User.class);
        SubscriptionFilterDto dtoFilter = mock(SubscriptionFilterDto.class);

        when(user.getPhone()).thenReturn("111222");
        when(dtoFilter.getPhonePattern()).thenReturn("111");

        boolean result = filters.apply(user, dtoFilter);
        assertTrue(result);
    }
}
