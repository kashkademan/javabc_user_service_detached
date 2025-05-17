package school.faang.user_service.mapper;

import lombok.Builder;
import school.faang.user_service.dto.MentorDto;
import school.faang.user_service.entity.User;

public class MentorsMapper {

    @Builder
    public static MentorDto toDto(User user) {
        return new MentorDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
