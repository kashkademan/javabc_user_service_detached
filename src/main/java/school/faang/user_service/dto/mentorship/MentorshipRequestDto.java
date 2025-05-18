package school.faang.user_service.dto.mentorship;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MentorshipRequestDto {

    @NotNull(message = "requesterId must not be null")
    private Long requesterId;

    @NotNull(message = "receiverId must not be null")
    private Long receiverId;

    @NotBlank(message = "description must not be empty")
    private String description;
}