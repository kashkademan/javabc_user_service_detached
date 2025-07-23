package school.faang.user_service.service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

/**
 * Фильтр пользователей по подстроке в электронной почте.
 * <p>
 * Реализация интерфейса {@link Filter}, позволяющая отфильтровать поток пользователей,
 * оставив только тех, чьи адреса электронной почты содержат указанную подстроку
 * (без учёта регистра).
 * </p>
 *
 * @author Myrza
 * @since 16.07.2025
 */
@Component
public class UserEmailContainsFilter implements Filter<User, UserFilterDto> {
    @Override
    public boolean isApplicable(UserFilterDto dto) {
        return dto.emailContains() != null;
    }

    @Override
    public Stream<User> filter(Stream<User> entities, UserFilterDto dto) {
        var target = dto.emailContains().toLowerCase();
        return entities.filter(entity -> {
            var email = entity.getEmail().toLowerCase();
            return email.contains(target);
        });
    }
}
