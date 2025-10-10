package school.faang.user_service.filters;

import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameFilterTest {
    private final NameFilter nameFilter = new NameFilter();
               //проверяем на соответ фильтру что имя заполнено
    @Test
    public void testIsApplicable_WhenNamePatternIsNotNullAndNotBlankReturnsTrue() {
        boolean result = nameFilter.isApplicable(new UserFiltersDto("name", null, 0, 0));
        assertTrue(result);
    }
                //проверка пустой фильтр по имени, assertFalse ФИЛЬТР НЕ ПРИМЕНИМ когда нет шаблона имени
    @Test
    public void testIsApplicable_WhenNamePatternEmptyReturnsFalse() {
        boolean result = nameFilter.isApplicable(new UserFiltersDto("", null, 0, 0));
        assertFalse(result);
    }
    //проверка пробелы фильтр по имени
    @Test
    public void testIsApplicable_WhenNamePatternIsBlankReturnsFalse() {
        boolean result = nameFilter.isApplicable(new UserFiltersDto(" ", null, 0, 0));
        assertFalse(result);
    }
   // проверка null фильтр по имени
    @Test
    public void testIsApplicable_WhenNamePatternIsNullReturnsFalse() {
        boolean result = nameFilter.isApplicable(new UserFiltersDto(null, null, 0, 0));
        assertFalse(result);
    }
    // проверка что найден 1 юзер и 2 проверка найден по имени Ira
    @Test
    public void testApply_WhenExactNameMatchReturnsSingleUser () {
        Stream<User> users = Stream.of(
                User.builder().username("Kirill").build(),
                User.builder().username("Ira").build()
        );
        Stream<User> result = nameFilter.apply(users, new UserFiltersDto("Ira", null, 0, 0));
        List<User> userList = result.toList();
        assertEquals(1, userList.size());
        assertEquals("Ira", userList.get(0).getUsername());
    }
   // найти все  все варианты написания
    @Test
    public void testApply_WhenCaseInsensitiveNameMatchReturnsAllMatchingUsers () {
        Stream<User> users = Stream.of(
                User.builder().username("Kirill").build(),
                User.builder().username("kirill").build(),
                User.builder().username("KIRILL").build()
        );
        Stream<User> user = nameFilter.apply(users, new UserFiltersDto("kIrill", null, 0, 0));
        List<User> userList = user.toList();
        assertEquals(3, userList.size());
        assertEquals("kirill", userList.get(0).getUsername().toLowerCase());
        assertEquals("kirill", userList.get(1).getUsername().toLowerCase());
        assertEquals("kirill", userList.get(2).getUsername().toLowerCase());
    }
}