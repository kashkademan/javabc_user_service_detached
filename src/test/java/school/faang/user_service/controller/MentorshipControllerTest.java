package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MentorshipControllerTest {

    @Mock
    private MentorshipService service;

    @InjectMocks
    private MentorshipController controller;

    private MockMvc mockMvc;

    private final long MENTOR_ID = 1L;
    private final long MENTEE_ID = 2L;
    private final UserDto userDto = new UserDto(
            1L,
            "testUser",
            "test@example.com",
            "+123456789",
            "About me"
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void addMentorship_Success() throws Exception {
        doNothing().when(service).addMentorship(MENTOR_ID, MENTEE_ID);

        mockMvc.perform(post("/mentorships/{mentorId}?menteeId={menteeId}", MENTOR_ID, MENTEE_ID))
                .andExpect(status().isOk());

        verify(service).addMentorship(MENTOR_ID, MENTEE_ID);
    }

    @Test
    void getMentees_Success() throws Exception {
        when(service.getMentees(MENTOR_ID)).thenReturn(List.of(userDto));

        mockMvc.perform(get("/mentorships/mentees/{userId}", MENTOR_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userDto.id()))
                .andExpect(jsonPath("$[0].username").value(userDto.username()));

        verify(service).getMentees(MENTOR_ID);
    }

    @Test
    void getMentors_Success() throws Exception {
        when(service.getMentors(MENTEE_ID)).thenReturn(List.of(userDto));

        mockMvc.perform(get("/mentorships/mentors/{userId}", MENTEE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userDto.id()))
                .andExpect(jsonPath("$[0].username").value(userDto.username()));

        verify(service).getMentors(MENTEE_ID);
    }

    @Test
    void deleteMentorship_Success() throws Exception {
        doNothing().when(service).deleteMentorship(MENTEE_ID, MENTOR_ID);

        mockMvc.perform(delete("/mentorships/{menteeId}?mentorId={mentorId}", MENTEE_ID, MENTOR_ID))
                .andExpect(status().isOk());

        verify(service).deleteMentorship(MENTEE_ID, MENTOR_ID);
    }
}
