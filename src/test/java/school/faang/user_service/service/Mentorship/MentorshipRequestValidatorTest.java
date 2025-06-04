package school.faang.user_service.service.Mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    private final long requesterId = 1L;
    private final long receiverId = 2L;
    private final List<User> mentees = List.of(
            User.builder()
                    .id(requesterId)
                    .build());

    @Test
    public void testValidateMentorshipRequestCooldown_ShouldThrowException_WhenRequestIsRecent() {
        LocalDateTime recent = LocalDateTime.now().minusMonths(1); // меньше 3 месяцев назад
        MentorshipRequest recentRequest = new MentorshipRequest();
        recentRequest.setCreatedAt(recent);

        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);

        when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(recentRequest));

        assertThrows(DataValidationException.class, () -> {
            mentorshipRequestValidator.validateMentorshipRequestCooldown(dto);
        });
    }

    @Test
    public void testValidateMentorshipRequestCooldown_ShouldPass_WhenNoRequestExists() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);

        when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> {
            mentorshipRequestValidator.validateMentorshipRequestCooldown(dto);
        });
    }

    @Test
    public void testValidateMentorshipRequestCooldown_ShouldPass_WhenRequestIsOld() {
        LocalDateTime old = LocalDateTime.now().minusMonths(4); // больше 3 месяцев назад
        MentorshipRequest oldRequest = new MentorshipRequest();
        oldRequest.setCreatedAt(old);

        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);

        when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(oldRequest));

        Assertions.assertDoesNotThrow(() -> {
            mentorshipRequestValidator.validateMentorshipRequestCooldown(dto);
        });
    }

    @Test
    public void testValidateDescriptionIsNotBlank() {
        String description = " ";
        assertThrows(DataValidationException.class, () -> mentorshipRequestValidator.validateDescriptionIsNotBlank(description));
    }

    @Test
    void testValidateNoAcceptedStatus() {
        RequestStatus status = RequestStatus.ACCEPTED;
        assertThrows(DataValidationException.class, () -> mentorshipRequestValidator.validateNoAcceptedStatus(status));
    }

    @Test
    void testValidateNotAlreadyMentor() {
        assertThrows(DataValidationException.class, () ->
                mentorshipRequestValidator.validateNotAlreadyMentor(mentees, requesterId));
    }

    @Test
    public void testValidateStatusNoReject(){
        RequestStatus status = RequestStatus.REJECTED;
        assertThrows(DataValidationException.class, () ->
                mentorshipRequestValidator.validateStatusNoReject(status));
    }
}