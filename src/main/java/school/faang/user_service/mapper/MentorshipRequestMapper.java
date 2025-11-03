package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);

    List<MentorshipRequestDto> toDtoList(List<MentorshipRequest> mentorshipRequestList);
}
