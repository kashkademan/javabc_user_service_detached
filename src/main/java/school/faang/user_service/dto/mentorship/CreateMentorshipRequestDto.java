package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;

public record CreateMentorshipRequestDto(
        @NotBlank(message = "Описание не может быть пустым")
        String description,
        Long mentorId
) {
}
