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

class UserPhonePatternFilterTest extends BaseUserFilterTest {

    @Spy
    private UserPhonePatternFilter filter = new UserPhonePatternFilter();

    @Test
    @DisplayName("Фильтр по телефону — фильтрует корректно")
    void testApplyFilterWhenPhonePatternIsSet() {
        UserFilterDto dto = new UserFilterDto();
        dto.setPhonePattern("123");

        Stream<User> input = Stream.of(user1, user2, user3);
        List<User> result = filter.apply(input, dto).toList();

        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getPhone());
    }

    @Test
    @DisplayName("Фильтр по телефону — пропускает, если параметр не задан")
    void testSkipFilterWhenPhonePatternIsNull() {
        UserFilterDto dto = new UserFilterDto();
        Stream<User> input = Stream.of(user1, user2);

        List<User> result = filter.apply(input, dto).toList();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Фильтр по телефону — phone = null")
    void testHandleNullPhone() {
        UserFilterDto dto = new UserFilterDto();
        dto.setPhonePattern("123");
        Stream<User> input = Stream.of(userNullPhone);

        List<User> result = filter.apply(input, dto).toList();

        assertTrue(result.isEmpty());
    }
}
