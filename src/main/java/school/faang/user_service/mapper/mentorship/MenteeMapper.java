
package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.GetMenteesResponseDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MenteeMapper {

    GetMenteesResponseDto toDto(User user);

    User toEntity(GetMenteesResponseDto dto);
}
