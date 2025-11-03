package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.fiters.ForTestMentorshipReceiverFilter;
import school.faang.user_service.service.mentorship.fiters.ForTestMentorshipRequesterFilter;
import school.faang.user_service.service.mentorship.fiters.ForTestMentorshipStatusFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Mock
    MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    UserContext userContext;

    @Spy
    MentorshipRequestMapperImpl mentorshipRequestMapper;

    @InjectMocks
    MentorshipRequestServiceImpl mentorshipRequestService;

    @BeforeEach
    public void setUp() {
        mentorshipRequestService.setMonthsLimit(3);
    }

    @Nested
    class CreatingMethods {

        CreateMentorshipRequestDto dto;

        @BeforeEach
        public void setUp() {
            dto = new CreateMentorshipRequestDto("test", 1L);
        }

        @Test
        public void testCreateWithInvalidDate() {
            MentorshipRequest request = MentorshipRequest.builder()
                    .createdAt(LocalDateTime.now().minusMonths(2))
                    .build();
            when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()))
                    .thenReturn(Optional.ofNullable(request));

            assertThrows(ForbiddenException.class, () -> mentorshipRequestService.create(dto));
        }

        @Test
        public void testCreate() {
            MentorshipRequest request = MentorshipRequest.builder()
                    .createdAt(LocalDateTime.now().minusMonths(4))
                    .build();
            when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()))
                    .thenReturn(Optional.ofNullable(request));

            mentorshipRequestService.create(dto);

            verify(mentorshipRequestRepository).create(anyLong(), anyLong(), anyString());
        }
    }

    @Nested
    class SetupFilters {
        MentorshipRequestFilterDto dto;
        List<MentorshipRequestFilter> requestFilters1;
        List<MentorshipRequestFilter> requestFilters2;
        List<MentorshipRequest> requests;
        MentorshipRequestServiceImpl requestService1;
        MentorshipRequestServiceImpl requestService2;

        @BeforeEach
        public void setUp() {
            User user1 = User.builder().id(1L).build();
            User user2 = User.builder().id(2L).build();
            User user3 = User.builder().id(3L).build();
            User user4 = User.builder().id(4L).build();
            MentorshipRequest request1 = MentorshipRequest.builder()
                    .requester(user1)
                    .receiver(user2)
                    .status(RequestStatus.ACCEPTED)
                    .build();
            MentorshipRequest request2 = MentorshipRequest.builder()
                    .requester(user3)
                    .receiver(user4)
                    .status(RequestStatus.ACCEPTED)
                    .build();
            requests = List.of(request1, request2);
            dto = new MentorshipRequestFilterDto(
                    null,
                    null,
                    null);
            MentorshipRequestFilter requestRequesterFilters = new ForTestMentorshipRequesterFilter();
            MentorshipRequestFilter receiverRequesterFilters = new ForTestMentorshipReceiverFilter();
            MentorshipRequestFilter statusRequesterFilters = new ForTestMentorshipStatusFilter();
            requestFilters1 = List.of(
                    requestRequesterFilters,
                    receiverRequesterFilters,
                    statusRequesterFilters);
            requestFilters2 = List.of(
                    statusRequesterFilters
            );
            requestService1 = new MentorshipRequestServiceImpl(
                    mentorshipRequestRepository,
                    userContext,
                    mentorshipRequestMapper,
                    requestFilters1);
            requestService2 = new MentorshipRequestServiceImpl(
                    mentorshipRequestRepository,
                    userContext,
                    mentorshipRequestMapper,
                    requestFilters2);
        }

        @Test
        public void testGetByRequesterId() {
            when(mentorshipRequestRepository.findAll()).thenReturn(requests);

            List<MentorshipRequestDto> filtered1 = requestService1.getByFilters(dto);
            List<MentorshipRequestDto> filtered2 = requestService2.getByFilters(dto);

            assertEquals(1, filtered1.size());
            assertEquals(2, filtered2.size());
        }

        @Test
        public void unsuccessfulAccept() {
            MentorshipRequest mentorshipRequest = requests.get(0);
            when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mentorshipRequest));
            when(userContext.getUserId()).thenReturn(requests.get(0).getReceiver().getId());

            assertThrows(ForbiddenException.class, () -> mentorshipRequestService.accept(mentorshipRequest.getId()));
        }

        @Test
        public void accept() {
            MentorshipRequest mentorshipRequest = requests.get(1);
            when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mentorshipRequest));
            when(userContext.getUserId()).thenReturn(requests.get(0).getReceiver().getId() + 1);

            mentorshipRequestService.accept(mentorshipRequest.getId());

            verify(mentorshipRequestRepository).save(any());
        }

        @Test
        public void testReject() {
            MentorshipRequest mentorshipRequest = requests.get(0);
            when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(requests.get(0)));

            mentorshipRequestService.accept(mentorshipRequest.getId());

            verify(mentorshipRequestRepository).save(any());
        }
    }
}