package school.faang.user_service.controller.mentorship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.mentorship.MentorshipRequestServiceImpl;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
            .description("some desc")
            .receiver(UserDto.builder().id(1L).build())
            .build();
    private final MentorshipRequestDto mentorshipRequestDtoTwo = MentorshipRequestDto.builder()
            .description("anything to input")
            .receiver(UserDto.builder().id(2L).build())
            .build();
    private MockMvc mockMvc;

    @Mock
    private MentorshipRequestServiceImpl mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorshipRequestController).build();
    }

    @Test
    void testCreate() throws Exception {
        CreateMentorshipRequestDto createMentorshipRequestDto = CreateMentorshipRequestDto.builder()
                .mentorId(mentorshipRequestDto.receiver().id())
                .description(mentorshipRequestDto.description())
                .build();

        when(mentorshipRequestService.create(createMentorshipRequestDto)).thenReturn(mentorshipRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/mentorshipRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMentorshipRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(MentorshipRequestDto.Fields.receiver + "." + UserDto.Fields.id,
                        Matchers.equalTo(createMentorshipRequestDto.mentorId().intValue())))
                .andExpect(jsonPath(MentorshipRequestDto.Fields.description,
                        Matchers.equalTo(createMentorshipRequestDto.description())));
    }

    @Test
    void testGetByFilters() throws Exception {
        List<MentorshipRequestDto> expectedMentorshipList = List.of(mentorshipRequestDto, mentorshipRequestDtoTwo);

        MentorshipRequestFilterDto mentorshipRequestFilterDto
                = new MentorshipRequestFilterDto(0L, 0L, RequestStatus.ACCEPTED);

        when(mentorshipRequestService.getByFilters(mentorshipRequestFilterDto)).thenReturn(expectedMentorshipList);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/mentorshipRequest")
                        .param("requesterId", mentorshipRequestFilterDto.requesterId().toString())
                        .param("receiverId", mentorshipRequestFilterDto.receiverId().toString())
                        .param("status", String.valueOf(mentorshipRequestFilterDto.status())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(expectedMentorshipList.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<MentorshipRequestDto> actualMentorshipList = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, MentorshipRequestDto.class));

        Assertions.assertTrue(actualMentorshipList.containsAll(expectedMentorshipList));
    }

    @Test
    void testAccept() throws Exception {
        long id = 1L;

        doNothing().when(mentorshipRequestService).accept(id);

        mockMvc.perform(MockMvcRequestBuilders.post("/mentorshipRequest/{requestId}/accept", id))
                .andExpect(status().isNoContent());

        verify(mentorshipRequestService).accept(id);
    }

    @Test
    void testReject() throws Exception {
        long id = 1L;
        RejectionDto rejectionDto = RejectionDto.builder()
                .reason("some reason")
                .build();

        doNothing().when(mentorshipRequestService).reject(id, rejectionDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/mentorshipRequest/{requestId}/reject", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionDto)))
                .andExpect(status().isNoContent());

        verify(mentorshipRequestService).reject(id, rejectionDto);
    }
}