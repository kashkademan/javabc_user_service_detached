package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillCreateRequestDto;
import school.faang.user_service.dto.skill.SkillResponseDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    Skill toEntity(SkillCreateRequestDto skillCreateRequestDto);
    @Mapping(target = "usersIds", expression = "java(mapUsersIds(skill.getUsers()))")
    @Mapping(target = "userSkillGuaranteesIds", expression = "java(mapUserSkillGuaranteesIds(skill.getGuarantees()))")
    @Mapping(target = "eventsIds", expression = "java(mapEventsIds(skill.getEvents()))")
    @Mapping(target = "goalsIds", expression = "java(mapGoalsIds(skill.getGoals()))")
    SkillResponseDto toSkillResponseDto(Skill skill);
    List<SkillResponseDto> toSkillResponseDtoList(List<Skill> skills);

    default List<Long> mapUsersIds(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(User::getId)
                .toList();
    }

    default List<Long> mapUserSkillGuaranteesIds(List<UserSkillGuarantee> userSkillGuarantees) {
        if (userSkillGuarantees == null) {
            return new ArrayList<>();
        }
        return userSkillGuarantees.stream()
                .map(UserSkillGuarantee::getId)
                .toList();
    }

    default List<Long> mapEventsIds(List<Event> events) {
        if (events == null) {
            return new ArrayList<>();
        }
        return events.stream()
                .map(Event::getId)
                .toList();
    }

    default List<Long> mapGoalsIds(List<Goal> goals) {
        if (goals == null) {
            return new ArrayList<>();
        }
        return goals.stream()
                .map(Goal::getId)
                .toList();
    }

    default SkillCandidateDto toSkillCandidateDto(Skill skill, long offersAmount) {
        return new SkillCandidateDto(toSkillResponseDto(skill), offersAmount);
    }
}
