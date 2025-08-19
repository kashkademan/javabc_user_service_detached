package school.faang.user_service.dto.analytics;

import jakarta.validation.constraints.Positive;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * ProfileVisitDto — неизменяемая структура данных (record).
 * <p>
 * TODO: описать предназначение record и его поля.
 * </p>
 *
 * @param visitorId описание первого поля
 * @author Myrza
 * @since 19.08.2025
 */
public record ProfileVisitCreateDto(
        @NonNull
        @Positive
        Long visitorId,
        @NonNull
        @Positive
        Long visitedId,
        @NonNull
        LocalDateTime visitedAt
) {
}
