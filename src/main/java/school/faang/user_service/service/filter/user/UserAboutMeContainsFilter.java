package school.faang.user_service.service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

/**
 * Фильтр пользователей по подстроке в поле "О себе" (aboutMe).
 * <p>
 * Реализация интерфейса {@link Filter}, позволяющая отфильтровать поток пользователей,
 * оставив только тех, чьё описание (aboutMe) содержит указанную подстроку
 * (без учёта регистра).
 * </p>
 *
 * @author Myrza
 * @since 16.07.2025
 */
@Component
public class UserAboutMeContainsFilter implements Filter<User, UserFilterDto> {
    @Override
    public boolean isApplicable(UserFilterDto dto) {
        return dto.aboutMeContains() != null;
    }

    @Override
    public Stream<User> filter(Stream<User> entities, UserFilterDto dto) {
        var target = dto.aboutMeContains().toLowerCase();
        return entities.filter(entity -> {
            var aboutMe = entity.getAboutMe().toLowerCase();
            return aboutMe.contains(target);
        });
    }
}
