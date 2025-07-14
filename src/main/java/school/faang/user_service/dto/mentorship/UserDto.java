package school.faang.user_service.dto.mentorship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.mentorshp.MentorshipRequest;
import school.faang.user_service.entity.user.User;

import java.util.List;

public record UserDto(
        @NotNull(message = "ID не может быть null")
        Long id,
        @JsonIgnore
        List<User> mentors,
        @JsonIgnore
        List<MentorshipRequest> sentMentorshipRequests
) {}
