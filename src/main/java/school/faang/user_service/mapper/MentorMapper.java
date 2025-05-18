package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MentorMapper {

    @Mapping(source = "username", target = "name")
    MentorDto toDto(User user);
}