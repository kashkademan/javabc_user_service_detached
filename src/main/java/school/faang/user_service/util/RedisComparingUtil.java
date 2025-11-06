package school.faang.user_service.util;

import org.springframework.data.domain.Sort;
import school.faang.user_service.dto.user.UserDto;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;


public class RedisComparingUtil {
    private static final Map<String, Function<UserDto, String>> FIELD_EXTRACTORS = Map.of(
            UserDto.Fields.id, userDto -> String.valueOf(userDto.id()),
            UserDto.Fields.username, UserDto::username,
            UserDto.Fields.email, UserDto::email,
            UserDto.Fields.phone, UserDto::phone,
            UserDto.Fields.aboutMe, UserDto::aboutMe
    );

    public static Comparator<UserDto> createComparatorFromSort(Sort sort) {
        return sort.stream()
                .map(RedisComparingUtil::createComparatorForOrder)
                .reduce(Comparator::thenComparing)
                .orElse(Comparator.comparing(UserDto::id));
    }

    private static Comparator<UserDto> createComparatorForOrder(Sort.Order order) {
        Function<UserDto, String> fieldExtractor = FIELD_EXTRACTORS.get(order.getProperty());

        Comparator<UserDto> comparator;

        comparator = Comparator.comparing(fieldExtractor, nullsLastIgnoreCase());

        return order.isAscending() ? comparator : comparator.reversed();
    }


    private static Comparator<String> nullsLastIgnoreCase() {
        return Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
    }
}
