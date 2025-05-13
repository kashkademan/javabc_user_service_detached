package school.faang.user_service.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;


class UserExperienceMaxFilterTest extends BaseUserFilterTest {

    @Spy
    private UserExperienceMaxFilter filter = new UserExperienceMaxFilter();

    @Test
    @DisplayName("Фильтр по максимальному опыту — фильтрует корректно")
    void applyFilterWhenMaxExperienceIsSet() {
        UserFilterDto dto = new UserFilterDto();
        dto.setExperienceMax(10);
        Stream<User> input = Stream.of(user1, user2, user3);

        List<User> result = filter.apply(input, dto).toList();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> u.getExperience() <= 10));
        verify(filter).apply(Mockito.any(), Mockito.eq(dto));
    }

    @Test
    @DisplayName("Фильтр по максимальному опыту — пропускает, если параметр не задан")
    void skipFilterWhenMaxExperienceIsNull() {
        UserFilterDto dto = new UserFilterDto();
        Stream<User> input = Stream.of(user1, user2, user3);

        List<User> result = filter.apply(input, dto).toList();

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Фильтр по максимальному опыту — граничное значение")
    void includeUserWithExactMaxExperience() {
        UserFilterDto dto = new UserFilterDto();
        dto.setExperienceMax(10);
        Stream<User> input = Stream.of(user2);

        List<User> result = filter.apply(input, dto).toList();

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getExperience());
    }
}