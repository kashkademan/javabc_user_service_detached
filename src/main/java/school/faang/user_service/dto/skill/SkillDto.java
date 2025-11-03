package school.faang.user_service.dto.skill;

import com.fasterxml.jackson.annotation.JsonProperty;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

public record SkillDto(
        @JsonProperty("id") Long id,
        @JsonProperty("title") String title,
        @JsonProperty("guarantors") List<UserDto> guarantors
) {
}
