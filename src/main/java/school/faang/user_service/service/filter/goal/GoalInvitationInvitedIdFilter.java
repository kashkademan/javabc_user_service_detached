package school.faang.user_service.service.filter.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

/**
 * Фильтр для сущностей {@link GoalInvitation}, отбирающий приглашения по идентификатору приглашённого пользователя.
 * <p>
 * Проверяет значение invitedId в {@link GoalInvitationFilterDto} и оставляет только те {@link GoalInvitation},
 * у которых идентификатор приглашённого совпадает с указанным значением.
 * </p>
 * <p>
 * Типичный сценарий использования:
 * <ul>
 *     <li>В параметрах фильтрации указан invitedId.</li>
 *     <li>Фильтр считает себя применимым (isApplicable возвращает {@code true}).</li>
 *     <li>В результате фильтруются только те {@code GoalInvitation},
 *     где invitedId совпадает с переданным значением.</li>
 * </ul>
 * </p>
 *
 * @see Filter
 * @see GoalInvitation
 * @see GoalInvitationFilterDto
 *
 * @author Myrza
 * @since 09.07.2025
 */
@Component
@RequiredArgsConstructor
public class GoalInvitationInvitedIdFilter implements Filter<GoalInvitation, GoalInvitationFilterDto> {

    @Override
    public boolean isApplicable(GoalInvitationFilterDto dto) {
        return dto.invitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> filter(Stream<GoalInvitation> entities, GoalInvitationFilterDto dto) {
        Long target = dto.invitedId();
        return entities.filter(entity -> entity.getInvited().getId().equals(target));
    }
}
