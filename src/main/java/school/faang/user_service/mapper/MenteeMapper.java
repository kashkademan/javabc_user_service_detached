package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MenteeMapper {
    MenteeDto toDto (User user);
}
