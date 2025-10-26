package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
public interface CreateGoalInvitationMapper {
    GoalInvitation toGoalInvitation(CreateGoalInvitationDto goalInvitationDto);
}
