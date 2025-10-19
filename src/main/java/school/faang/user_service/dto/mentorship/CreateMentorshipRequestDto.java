package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMentorshipRequestDto(

        @NotBlank(message = "Description should be present")
        @Size(max = 1000, message = "Description length cant be more than 1000 character")
        String description,
        @NotNull(message = "Mentor id should be present")
        Long mentorId
) {
}