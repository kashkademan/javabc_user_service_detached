package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface MentorshipRequestMapper {
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    MentorshipRequest toMentorshipRequestEntity(MentorshipRequestDto mentorshipRequestDto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    List<MentorshipRequestDto> toMentorshipRequestDtoList(List<MentorshipRequest> mentorshipRequests);
}