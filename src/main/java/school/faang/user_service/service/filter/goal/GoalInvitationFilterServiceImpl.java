package school.faang.user_service.service.filter.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.Filter;
import school.faang.user_service.service.filter.FilterService;

import java.util.List;

/**
 * Реализация сервиса фильтрации приглашений в цель ({@link GoalInvitation}) с использованием набора фильтров.
 * <p>
 * Применяет коллекцию фильтров {@link Filter}, каждый из которых знает, когда он применим
 * к переданным параметрам фильтрации ({@link GoalInvitationFilterDto}), и выполняет
 * собственную логику фильтрации.
 * </p>
 * <p>
 * Последовательность работы:
 * <ul>
 *     <li>В конструктор или иным способом передается список фильтров.</li>
 *     <li>При вызове {@code getFilteredList} сервис проверяет входной список на null или пустоту.</li>
 *     <li>Затем применяет только те фильтры из списка, которые возвращают
 *     {@code true} при вызове {@link Filter#isApplicable(Object)}.</li>
 *     <li>Фильтры применяются последовательно, формируя итоговый результат.</li>
 * </ul>
 * </p>
 *
 * @author Myrza
 * @see FilterService
 * @see Filter
 * @see GoalInvitation
 * @see GoalInvitationFilterDto
 * @since 09.07.2025
 */
@Component
@RequiredArgsConstructor
public class GoalInvitationFilterServiceImpl implements FilterService<GoalInvitation, GoalInvitationFilterDto> {
    private final List<Filter<GoalInvitation, GoalInvitationFilterDto>> filters;

    @Override
    public List<GoalInvitation> getFilteredList(List<GoalInvitation> entities, GoalInvitationFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}
