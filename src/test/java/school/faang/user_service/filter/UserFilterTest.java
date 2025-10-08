package school.faang.user_service.filter;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserFilterTest {

    private final UserExperienceFilter userFilter = new UserExperienceFilter();

    @Test
    public void testApplicableTrue() {
        boolean result = userFilter.isApplicable(
            new UserFiltersDto("Joe", "987987", 1, 5));
        assertTrue(result);
    }

    @Test
    public void testApplicableFalse() {
        boolean result = userFilter.isApplicable(
            new UserFiltersDto(null, null, 5, 1));
        assertFalse(result);
    }

    @Test
    public void testApply() {
        User user1 = User.builder()
                .id(1L)
                .username("Joe")
                .phone("8787")
                .experience(4)
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("Joe")
                .phone("8788")
                .experience(3)
                .build();
        User user3 = User.builder()
                .id(3L)
                .username("Jane")
                .phone("8789")
                .experience(2)
                .build();
        User user4 = User.builder()
                .id(4L)
                .username("John")
                .phone("8790")
                .experience(5)
                .build();

        Stream<User> usersStream = Stream.of(user1, user2, user3, user4);

        UserFiltersDto filtersDto = new UserFiltersDto(null, null, 3, 4);

        Stream<User> filteredUsersStream = userFilter.apply(usersStream, filtersDto);
        List<User> filteredUsers = filteredUsersStream.collect(Collectors.toList());

        assertEquals(2, filteredUsers.size());
        assertEquals("Joe", filteredUsers.get(0).getUsername());
        assertEquals(4, filteredUsers.get(0).getExperience());
        assertEquals("Joe", filteredUsers.get(1).getUsername());
        assertEquals(3, filteredUsers.get(1).getExperience());
    }

    @Test
    public void testApplyNoMatch() {
        User user1 = User.builder()
                .id(1L)
                .username("Joe")
                .phone("8787")
                .experience(4)
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("Jane")
                .phone("8788")
                .experience(3)
                .build();

        Stream<User> usersStream = Stream.of(user1, user2);

        UserFiltersDto filtersDto = new UserFiltersDto(null, null, 5, 10);

        Stream<User> filteredUsersStream = userFilter.apply(usersStream, filtersDto);
        List<User> filteredUsers = filteredUsersStream.collect(Collectors.toList());

        assertEquals(0, filteredUsers.size());
    }
}
