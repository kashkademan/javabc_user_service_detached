package school.faang.user_service.dto.analytics;

import java.time.LocalDateTime;

/**
 * ProfileVisitViewDto — неизменяемая структура данных (record).
 * <p>
 * TODO: описать предназначение record и его поля.
 * </p>
 *
 * @param id описание первого поля
 * @author Myrza
 * @since 19.08.2025
 */
public record ProfileVisitViewDto(
        Long id,
        Long visitorId,
        Long visitedId,
        LocalDateTime visitedAt
) {
}
