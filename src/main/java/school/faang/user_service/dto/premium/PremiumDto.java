package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * PremiumDto — Data Transfer Object для передачи информации о премиум-подписке пользователя.
 * <p>
 * Используется для обмена данными между слоями приложения и внешними клиентами.
 * </p>
 *
 * @author agent
 * @since 10.07.2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumDto {

    private Long id;

    private Long userId;

    private LocalDate startDate;

    private LocalDate endDate;
}