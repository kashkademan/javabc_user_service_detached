package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.team.TeamDto;
import school.faang.user_service.entity.team.Team;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TeamMapper {

    @Mapping(target = "avatarUrl", source = "team", qualifiedByName = "mapAvatarUrl")
    TeamDto toTeamDto(Team team);

    @Named("mapAvatarUrl")
    default String mapAvatarUrl(Team team) {
        return team.getAvatarKey() != null ?
                "/api/teams/" + team.getId() + "/avatar" : null;
    }
}