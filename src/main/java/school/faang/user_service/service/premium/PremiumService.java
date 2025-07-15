package school.faang.user_service.service.premium;

import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.UserWithPremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriodEnum;

import java.util.List;

/**
 * PremiumService — интерфейс сервиса для управления премиум-подписками пользователей.
 * <p>
 * Предоставляет методы для покупки премиум-доступа и получения списка пользователей с активной подпиской.
 * </p>
 *
 * @author agent
 * @since 10.07.2025
 */
public interface PremiumService {

    /**
     * Оформляет покупку премиум-подписки для пользователя на указанный период.
     * <p>
     * Проверяет, что у пользователя ещё нет активного премиума,
     * затем инициирует платёж через внешний сервис и при успехе сохраняет подписку.
     * </p>
     *
     * @param userId идентификатор пользователя, который покупает премиум
     * @param period период премиум-подписки (содержит длительность и цену)
     * @return DTO с информацией о созданной премиум-подписке
     */
    PremiumDto buyPremium(Long userId, PremiumPeriodEnum period);

    /**
     * Получает список пользователей с активной премиум-подпиской.
     *
     * @return список DTO пользователей с информацией о премиуме и оставшемся времени подписки
     */
    List<UserWithPremiumDto> getUsersWithActivePremium();
}
