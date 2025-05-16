package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring")
public interface RequestToResponseDto {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MentorshipResponseDto toDto(MentorshipRequest request);
}
