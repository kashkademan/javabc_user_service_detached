package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Маппер для преобразования между {@link GoalInvitation} и {@link GoalInvitationDto}.
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
     * Преобразует объект {@link GoalInvitationDto} в сущность {@link GoalInvitation}.
     *
     * @param goalInvitationDto DTO для преобразования
     * @return соответствующая сущность GoalInvitation
     */
    GoalInvitation toGoalInvitation(GoalInvitationDto goalInvitationDto);

    /**
     * Преобразует сущность {@link GoalInvitation} в DTO {@link GoalInvitationDto}.
     *
     * @param goalInvitation сущность для преобразования
     * @return соответствующий DTO GoalInvitationDto
     */
    GoalInvitationDto toGoalInvitationDto(GoalInvitation goalInvitation);
}
