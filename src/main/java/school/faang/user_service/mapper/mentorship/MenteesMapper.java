package school.faang.user_service.mapper.mentorship;

import lombok.Builder;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.entity.User;

public class MenteesMapper {

    @Builder
    public static MenteeDto toDto(User user) {
        return new MenteeDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
