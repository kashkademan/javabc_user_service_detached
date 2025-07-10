package school.faang.user_service.service.filter.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

/**
 * Фильтр для сущностей {@link GoalInvitation}, отбирающий приглашения по идентификатору пригласившего пользователя.
 * <p>
 * Проверяет поле inviterId в {@link GoalInvitationFilterDto} и оставляет только те приглашения,
 * у которых идентификатор пригласившего совпадает с указанным значением.
 * </p>
 * <p>
 * Пример сценария использования:
 * <ul>
 *     <li>В параметрах фильтрации задан inviterId.</li>
 *     <li>Фильтр считает себя применимым (isApplicable возвращает {@code true}).</li>
 *     <li>В результате фильтруются только те {@code GoalInvitation}, где inviterId совпадает с указанным.</li>
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
public class GoalInvitationInviterIdFilter implements Filter<GoalInvitation, GoalInvitationFilterDto> {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto dto) {
        return dto.inviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> filter(Stream<GoalInvitation> entities, GoalInvitationFilterDto dto) {
        Long targetInviterId = dto.inviterId();
        return entities.filter(entity -> targetInviterId.equals(entity.getInviter().getId()));
    }
}
