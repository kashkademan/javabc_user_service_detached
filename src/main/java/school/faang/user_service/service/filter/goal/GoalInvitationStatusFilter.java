package school.faang.user_service.service.filter.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

/**
 * Фильтр для сущностей {@link GoalInvitation}, отбирающий приглашения по статусу.
 * <p>
 * Проверяет поле {@code status} в {@link GoalInvitationFilterDto} и оставляет только те {@link GoalInvitation},
 * у которых статус совпадает с указанным значением.
 * </p>
 * <p>
 * Типичный сценарий использования:
 * <ul>
 *     <li>В параметрах фильтрации задан статус.</li>
 *     <li>Фильтр считает себя применимым (метод {@link #isApplicable} возвращает {@code true}).</li>
 *     <li>В результате остаются только те {@code GoalInvitation}, у которых статус совпадает с фильтром.</li>
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
public class GoalInvitationStatusFilter implements Filter<GoalInvitation, GoalInvitationFilterDto> {

    @Override
    public boolean isApplicable(GoalInvitationFilterDto dto) {
        return dto.status() != null;
    }

    @Override
    public Stream<GoalInvitation> filter(Stream<GoalInvitation> entities, GoalInvitationFilterDto dto) {
        RequestStatus target = dto.status();
        return entities.filter(entity -> entity.getStatus().equals(target));
    }
}
