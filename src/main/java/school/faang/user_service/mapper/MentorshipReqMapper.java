package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper
public interface MentorshipReqMapper {

    @Mapper(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    public interface MentorshipMapper {

        MentorshipMapper INSTANCE = Mappers.getMapper(MentorshipMapper.class);

        @Mapping(target = "requesterId", source = "requester.id")
        @Mapping(target = "receiverId", source = "receiver.id")
        @Mapping(target = "description", source = "description")
        @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
        MentorshipRequestDto toDto(MentorshipRequest entity);

    }
}
