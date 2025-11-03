package school.faang.user_service.dto.skill;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SkillCandidateDto(
        @JsonProperty("skill") SkillDto skill,
        @JsonProperty("offersAmount") Long offersAmount
) {
}
