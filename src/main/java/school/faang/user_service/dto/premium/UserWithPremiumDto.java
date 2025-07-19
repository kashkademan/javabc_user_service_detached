package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserWithPremiumDto — DTO для передачи информации о пользователе вместе с данными о его премиум-подписке.
 * <p>
 * Используется для отображения полной информации о пользователе
 * и его статусе премиум-подписки в клиентских приложениях и API.
 * </p>
 *
 * @author agent
 * @since 10.07.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithPremiumDto {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private LocalDateTime premiumStartDate;
    private LocalDateTime premiumEndDate;
    private Long remainingDays;
}
