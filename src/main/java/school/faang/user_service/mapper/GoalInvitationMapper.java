package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.goal.GoalInvitationViewDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Маппер для преобразования между {@link GoalInvitation} и {@link GoalInvitationViewDto}.
 * <p>
 * Использует MapStruct для автоматической генерации реализации во время сборки.
 * Настроен для использования с Spring (componentModel = SPRING)
 * и игнорирует несопоставленные свойства цели (unmappedTargetPolicy = IGNORE).
 * </p>
 *
 * @author Myrza
 * @since 07.07.2025
 */
@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public interface GoalInvitationMapper {
    /**
     * Преобразует сущность {@link GoalInvitation} в DTO {@link GoalInvitationViewDto}.
     *
     * @param goalInvitation сущность для преобразования
     * @return соответствующий DTO GoalInvitationDto
     */
    GoalInvitationViewDto toViewDto(GoalInvitation goalInvitation);
}
