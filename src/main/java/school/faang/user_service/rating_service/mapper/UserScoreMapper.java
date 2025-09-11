package school.faang.user_service.rating_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.rating_service.dto.UserScoreViewDto;
import school.faang.user_service.rating_service.dto.UserScoreProjection;

/**
 * Маппер для преобразования проекции в DTO
 *
 * @author Linempy
 * @since 11.09.2025
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserScoreMapper {

    UserScoreViewDto toDto(UserScoreProjection projection);

    default UserScoreViewDto getDtoByFields(Long userId, Double score) {
        return new UserScoreViewDto(userId, score);
    }
}