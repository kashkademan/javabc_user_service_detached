package school.faang.user_service.messaging.dto;

import java.time.LocalDateTime;

/**
 * SearchAppearanceEvent — неизменяемая структура данных (record).
 * <p>
 * TODO: описать предназначение record и его поля.
 * </p>
 *
 * @param id описание первого поля
 * @author Myrza
 * @since 19.08.2025
 */
public record SearchAppearanceEvent(
        Long id,
        Long viewerId,
        LocalDateTime date
) {
}
