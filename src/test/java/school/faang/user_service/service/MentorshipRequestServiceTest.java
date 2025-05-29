package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipResponseMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.Validator;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Spy
    private MentorshipResponseMapperImpl mentorshipResponseMapper;

    @Mock
    private List<Validator<MentorshipRequestDto>> validators;

    @Test
    @DisplayName("Checking request persistence in the database")
    public void testRequestIsSaved() {

        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "Test description");
        //поменять на builder
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        LocalDateTime fixedTime = LocalDateTime.of(2025, 5, 25, 10, 0);

        //поменять на builder
        MentorshipRequest saved = new MentorshipRequest();
        saved.setId(100L);
        saved.setRequester(requester);
        saved.setReceiver(receiver);
        saved.setDescription("Test description");
        saved.setStatus(RequestStatus.PENDING);
        saved.setCreatedAt(fixedTime);

        Mockito.when(mentorshipRequestRepository.create(1L, 2L, "Test description"))
                .thenReturn(saved);

        MentorshipResponseDto actualResponse = mentorshipRequestService.requestMentorship(dto);
        MentorshipResponseDto expectedResponse = new MentorshipResponseDto(
                100,
                1L,
                2L,
                "PENDING",
                "Test description",
                fixedTime
        );

        Assertions.assertEquals(expectedResponse, actualResponse);
        verify(mentorshipRequestRepository, times(1)).create(1L, 2L, "Test description");
        verifyNoMoreInteractions(mentorshipRequestRepository);
    }
}
