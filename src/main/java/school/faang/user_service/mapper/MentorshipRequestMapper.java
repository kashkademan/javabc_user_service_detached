package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.MentorshipRequestViewDto;
import school.faang.user_service.entity.user.MentorshipRequest;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {

    MentorshipRequestViewDto toEntity(MentorshipRequest mentorshipRequest);
}