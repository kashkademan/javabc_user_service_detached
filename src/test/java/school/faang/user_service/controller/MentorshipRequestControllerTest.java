package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.mentorship.MentorshipRequestController;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {
    MockMvc mockMvc;
    @Mock
    MentorshipRequestService mentorshipRequestService;
    @InjectMocks
    MentorshipRequestController mentorshipRequestController;

    ObjectMapper objectMapper = new ObjectMapper();
    MentorshipRequestDto dto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(mentorshipRequestController)
                .build();
        dto = new MentorshipRequestDto(
                1L,
                "test",
                null,
                null,
                null
        );
    }

    @Test
    public void testToMentorshipRequestDto() throws Exception {
        CreateMentorshipRequestDto requestDto = new CreateMentorshipRequestDto(
                "test",
                1L
        );
        when(mentorshipRequestService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/mentorship-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testGetByFilters() throws Exception {

        when(mentorshipRequestService.getByFilters(any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/mentorship-requests?receiverId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testAccept() {
        long requestId = 1L;
        when(mentorshipRequestService.accept(requestId))
                .thenReturn(MentorshipRequestDto.builder()
                        .id(requestId)
                        .status(RequestStatus.ACCEPTED)
                        .build());
        assertDoesNotThrow(() -> mentorshipRequestController.accept(requestId));

        verify(mentorshipRequestService, times(1)).accept(requestId);
    }

    @Test
    public void testReject() {
        long requestId = 1L;
        RejectionDto rejectRequestDto = new RejectionDto("test");
        when(mentorshipRequestService.reject(requestId, rejectRequestDto))
                .thenReturn(MentorshipRequestDto.builder()
                        .id(requestId)
                        .status(RequestStatus.REJECTED)
                        .build());

        assertDoesNotThrow(() -> mentorshipRequestController.reject(requestId, rejectRequestDto));

        verify(mentorshipRequestService, times(1)).reject(anyLong(), any());
    }

}
