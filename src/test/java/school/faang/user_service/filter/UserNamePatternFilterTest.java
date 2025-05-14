package school.faang.user_service.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserNamePatternFilterTest extends BaseUserFilterTest {

    @Spy
    private UserNamePatternFilter filter = new UserNamePatternFilter();

    @Test
    @DisplayName("Фильтр по имени — фильтрует корректно")
    void testApply_whenNamePatternSet_thenReturnMatchingUsers() {
        UserFilterDto dto = new UserFilterDto();
        dto.setNamePattern("User1");
        Stream<User> input = Stream.of(user1, user2, user3, userNullName);

        List<User> result = filter.apply(input, dto).toList();

        assertEquals(1, result.size());
        assertEquals("User1", result.get(0).getUsername());
    }

    @Test
    @DisplayName("Фильтр по имени — пропускает, если параметр не задан")
    void testApply_whenNamePatternIsNull_thenSkipFiltering() {
        UserFilterDto dto = new UserFilterDto();
        Stream<User> input = Stream.of(user1, user2);

        List<User> result = filter.apply(input, dto).toList();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Фильтр по имени — username = null")
    void testApply_whenUserNameIsNull_thenExcludeFromResult() {
        UserFilterDto dto = new UserFilterDto();
        dto.setNamePattern("Test");
        Stream<User> input = Stream.of(userNullName);

        List<User> result = filter.apply(input, dto).toList();

        assertTrue(result.isEmpty());
    }
}
