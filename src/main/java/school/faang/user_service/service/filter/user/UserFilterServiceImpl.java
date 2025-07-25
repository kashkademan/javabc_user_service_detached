package school.faang.user_service.service.filter.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.service.filter.Filter;
import school.faang.user_service.service.filter.FilterService;

import java.util.List;

/**
 * Реализация сервиса фильтрации пользователей.
 * <p>
 * Использует список фильтров для последовательного применения их к списку пользователей
 * в соответствии с параметрами фильтрации.
 * <p>
 *
 * @author Myrza
 * @since 16.07.2025
 */
@Component
@RequiredArgsConstructor
public class UserFilterServiceImpl implements FilterService<User, UserFilterDto> {
    private final List<Filter<User, UserFilterDto>> filters;

    @Override
    public List<User> getFilteredList(List<User> entities, UserFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}
