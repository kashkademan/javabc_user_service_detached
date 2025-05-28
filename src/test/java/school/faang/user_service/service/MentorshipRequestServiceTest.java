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
import school.faang.user_service.mapper.mentorship.MentorshipResponseMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Spy
    private MentorshipResponseMapper mentorshipResponseMapper;



    @Test
    @DisplayName("Проверка сохранения запроса в БД")
    public void testRequestIsSaved() {

        MentorshipRequestDto dto = new MentorshipRequestDto(1L,2L,"Это тестовое описание");

        MentorshipRequest entity = new MentorshipRequest();
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        LocalDateTime fixedTime = LocalDateTime.of(2025, 5, 25, 10, 0);

        entity.setRequester(requester);
        entity.setReceiver(receiver);
        entity.setDescription("Это тестовое описание");
        entity.setStatus(RequestStatus.PENDING);

        MentorshipRequest saved = new MentorshipRequest();
        saved.setId(100L);
        saved.setRequester(requester);
        saved.setReceiver(receiver);
        saved.setDescription("Это тестовое описание");
        saved.setStatus(RequestStatus.PENDING);
        saved.setCreatedAt(fixedTime);

        Mockito.when(mentorshipRequestRepository.create(1L, 2L, "Это тестовое описание"))
                .thenReturn(saved);

        MentorshipResponseDto actualResponse = mentorshipRequestService.requestMentorship(dto);
        MentorshipResponseDto expectedResponse = new MentorshipResponseDto(
                100,
                1L,
                2L,
                "PENDING",
                "Это тестовое описание",
                fixedTime
        );
        Assertions.assertEquals(expectedResponse, actualResponse);
    }
}
