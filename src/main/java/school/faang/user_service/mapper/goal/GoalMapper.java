package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalCreateRequestDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.GoalUpdateRequestDto;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.model.goal.GoalFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    Goal toGoalEntity(GoalCreateRequestDto goalCreateRequestDto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "createdAt", target = "createdDate")
    @Mapping(source = "updatedAt", target = "updatedDate")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(target = "invitationsIds", expression = "java(mapInvitationIds(goal.getInvitations()))")
    @Mapping(target = "usersIds", expression = "java(mapUserIds(goal.getUsers()))")
    @Mapping(target = "skillToAchieveIds", expression = "java(mapSkillIds(goal.getSkillsToAchieve()))")
    GoalResponseDto toGoalResponseDto(Goal goal);

    void update(@MappingTarget Goal goal, GoalUpdateRequestDto goalUpdateRequestDto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "createdAt", target = "createdDate")
    @Mapping(source = "updatedAt", target = "updatedDate")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(target = "invitationsIds", expression = "java(mapInvitationIds(goal.getInvitations()))")
    @Mapping(target = "usersIds", expression = "java(mapUserIds(goal.getUsers()))")
    @Mapping(target = "skillToAchieveIds", expression = "java(mapSkillIds(goal.getSkillsToAchieve()))")
    List<GoalResponseDto> toGoalResponseDtoList(List<Goal> goals);

    GoalFilter toGoalFilter(GoalFilterDto goalFilterDto);

    default List<Long> mapInvitationIds(List<GoalInvitation> invitations) {
        if (invitations == null) {
            return new ArrayList<>();
        }
        return invitations.stream()
                .map(GoalInvitation::getId)
                .toList();
    }

    default List<Long> mapUserIds(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(User::getId)
                .toList();
    }

    default List<Long> mapSkillIds(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}
