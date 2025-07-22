package school.faang.user_service.dto.skill;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SkillCandidateDto(
        @NotNull(message = "Should be present!")
        @NotBlank(message = "Should be present!")
        SkillDto skill,
        @NotNull(message = "Should be present!")
        @NotBlank(message = "Should be present!")
        int offersAmount
) {
}
