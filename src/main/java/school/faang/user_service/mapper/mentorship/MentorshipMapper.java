package school.faang.user_service.mapper.mentorship;

import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;

public class MentorshipMapper {

    public static MenteeDto toMenteeDto(User user) {
        return new MenteeDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public static MentorDto toMentorDto(User user) {
        return new MentorDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
