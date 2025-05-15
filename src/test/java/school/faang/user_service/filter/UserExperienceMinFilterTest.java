package school.faang.user_service.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserExperienceMinFilterTest extends BaseUserFilterTest {

    @Spy
    private UserExperienceMinFilter filter = new UserExperienceMinFilter();

    @Test
    @DisplayName("Фильтр по минимальному опыту — фильтрует корректно")
    void testApply_whenMinExperienceSet_thenReturnMatchingUsers() {
        UserFilterDto dto = new UserFilterDto();
        dto.setExperienceMin(10);
        Stream<User> input = Stream.of(user1, user2, user3);

        List<User> result = filter.apply(input, dto).toList();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(user -> user.getExperience() >= 10));
    }

    @Test
    @DisplayName("isApplicable возвращает false, если параметр не задан")
    void isApplicable_whenMinExperienceIsNull_thenReturnFalse() {
        UserFilterDto dto = new UserFilterDto();

        boolean result = filter.isApplicable(dto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр по минимальному опыту — граничное значение")
    void testApply_whenUserHasExactMinExperience_thenIncludeUser() {
        UserFilterDto dto = new UserFilterDto();
        dto.setExperienceMin(10);
        Stream<User> input = Stream.of(user2);

        List<User> result = filter.apply(input, dto).toList();

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getExperience());
    }
}
