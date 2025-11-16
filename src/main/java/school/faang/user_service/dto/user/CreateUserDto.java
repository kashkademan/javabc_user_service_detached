package school.faang.user_service.dto.user;

import school.faang.user_service.entity.user.Skill;

import java.util.List;

public record CreateUserDto(
        String username,
        String email,
        String password,
        Long countryId,
        List<Skill> skills
) {
}
