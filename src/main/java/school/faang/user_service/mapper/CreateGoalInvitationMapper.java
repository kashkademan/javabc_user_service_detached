package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
public interface CreateGoalInvitationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "invited", ignore = true)
    @Mapping(target = "inviter", ignore = true)
    @Mapping(target = "status", expression = "java(school.faang.user_service.entity.RequestStatus.PENDING)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GoalInvitation toGoalInvitation(CreateGoalInvitationDto goalInvitationDto);
}
