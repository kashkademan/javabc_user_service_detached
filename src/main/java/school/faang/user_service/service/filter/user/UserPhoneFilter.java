package school.faang.user_service.service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

/**
 * Фильтр пользователей по номеру телефона.
 * <p>
 * Реализация интерфейса {@link Filter}, которая отбирает пользователей,
 * чей номер телефона точно совпадает с указанным в параметрах фильтрации.
 * </p>
 *
 * @author Myrza
 * @since 16.07.2025
 */
@Component
public class UserPhoneFilter implements Filter<User, UserFilterDto> {
    @Override
    public boolean isApplicable(UserFilterDto dto) {
        return dto.phone() != null;
    }

    @Override
    public Stream<User> filter(Stream<User> entities, UserFilterDto dto) {
        var target = dto.phone();
        return entities.filter(entity -> entity.getPhone().equals(target));
    }
}
