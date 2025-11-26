package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.ProfilePicEventDto;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfilePicEventMapper {

    @Mapping(source = "userProfilePic.fileId", target = "profilePicFileId")
    @Mapping(source = "userProfilePic.smallFileId", target = "profilePicSmallFileId")
    ProfilePicEventDto toDto(User user);
}
