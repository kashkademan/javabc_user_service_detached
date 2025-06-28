package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {
    @Mock
    private MentorshipRequestService service;

    @InjectMocks
    private MentorshipRequestController controller;

    @Test
    public void testRequestMentorship_whenValidParameters_thenReturnCorrectValue() {
        when(service.requestMentorship(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .description("Description")
                .requesterId(1L)
                .receiverId(2L)
                .build();

        MentorshipRequestDto returnedValue = controller.requestMentorship(mentorshipRequestDto);

        assertNotNull(returnedValue);
        assertEquals(mentorshipRequestDto.getRequesterId(), returnedValue.getRequesterId());
        assertEquals(mentorshipRequestDto.getReceiverId(), returnedValue.getReceiverId());
        assertEquals(mentorshipRequestDto.getDescription(), returnedValue.getDescription());
    }

    @Test
    public void testGetRequests_whenValidParameters_thenReturnNotEmptyList() {
        List<MentorshipRequestDto> requests = List.of(
                MentorshipRequestDto.builder().build(),
                MentorshipRequestDto.builder().build()
        );
        when(service.getRequests(any()))
                .thenReturn(requests);

        List<MentorshipRequestDto> returnedRequests = controller.getRequests(RequestFilterDto.builder().build());

        assertEquals(requests.size(), returnedRequests.size());
        assertEquals(requests, returnedRequests);
    }

    @Test
    public void testAcceptRequest_whenValidParameters_thenReturnAcceptedRequest() {
        when(service.acceptRequest(any()))
                .thenReturn(MentorshipRequestDto.builder().id(1L).build());

        MentorshipRequestDto returned = controller.acceptRequest(1L);

        assertNotNull(returned);
        assertEquals(1L, returned.getId());
    }

    @Test
    public void testRejectRequest_whenValidParameters_thenReturnRejectedRequest() {
        when(service.rejectRequest(any(), any()))
                .thenReturn(MentorshipRequestDto.builder()
                        .id(1L)
                        .requestStatus(RequestStatus.REJECTED)
                        .rejectionReason("rejected")
                        .build()
                );

        MentorshipRequestDto returned = controller.rejectRequest(1L, RejectionDto.builder().build());

        assertNotNull(returned);
        assertEquals(1L, returned.getId());
        assertEquals(RequestStatus.REJECTED, returned.getRequestStatus());
        assertEquals("rejected", returned.getRejectionReason());
    }
}