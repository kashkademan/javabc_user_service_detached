package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestViewDto {

    @NotNull
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String description;

    @NotNull
    private UserDto requester;

    @NotNull
    private UserDto receiver;

    @NotNull
    private RequestStatus status;
}