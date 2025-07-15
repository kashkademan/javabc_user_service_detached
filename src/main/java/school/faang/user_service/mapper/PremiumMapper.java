package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.UserWithPremiumDto;
import school.faang.user_service.entity.premium.Premium;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * PremiumMapper — интерфейс для маппинга сущностей Premium в DTO объекты.
 * <p>
 * Использует MapStruct для автоматической генерации реализации.
 * Позволяет конвертировать сущность {@link Premium} в DTO {@link PremiumDto} и
 * {@link UserWithPremiumDto} с необходимыми полями пользователя и информации о премиуме.
 * </p>
 * <p>
 * Метод {@link #toDto(Premium)} преобразует {@link Premium} в {@link PremiumDto},
 * при этом извлекая идентификатор пользователя.
 * Метод {@link #toUserWithPremiumDto(Premium)} создаёт {@link UserWithPremiumDto},
 * содержащий данные пользователя и даты премиум-подписки.
 * </p>
 *
 * @author agent
 * @since 10.07.2025
 */
@Mapper(componentModel = "spring")
public interface PremiumMapper {

    @Mapping(source = "user.id", target = "userId")
    PremiumDto toDto(Premium premium);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.aboutMe", target = "aboutMe")
    @Mapping(source = "startDate", target = "premiumStartDate")
    @Mapping(source = "endDate", target = "premiumEndDate")
    UserWithPremiumDto toUserWithPremiumDto(Premium premium);

    default UserWithPremiumDto toUserWithRemainingDays(Premium premium) {
        UserWithPremiumDto dto = toUserWithPremiumDto(premium);
        long remainingDays = Duration.between(LocalDateTime.now(), premium.getEndDate()).toDays();
        if (remainingDays < 0) {
            remainingDays = 0L;
        }
        dto.setRemainingDays(remainingDays);
        return dto;
    }
}