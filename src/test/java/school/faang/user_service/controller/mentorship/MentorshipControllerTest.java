package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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

    private long mentorId = 1L;
    private long menteeId = 2L;
    private final UserDto userDto = new UserDto(
            1L,
            "testUser",
            "test@example.com",
            List.of(1L),
            "+123456789",
            "About me",
            null
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void addMentorshipTest() throws Exception {
        doNothing().when(service).addMentorship(mentorId, menteeId);

        mockMvc.perform(post("/mentorships/{mentorId}?menteeId={menteeId}", mentorId, menteeId))
                .andExpect(status().isOk());

        verify(service).addMentorship(mentorId, menteeId);
    }

    @Test
    void getMenteesTest() throws Exception {
        when(service.getMentees(mentorId)).thenReturn(List.of(userDto));

        mockMvc.perform(get("/mentorships/mentees/{userId}", mentorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userDto.id()))
                .andExpect(jsonPath("$[0].username").value(userDto.username()));

        verify(service).getMentees(mentorId);
    }

    @Test
    void getMentorsTest() throws Exception {
        when(service.getMentors(menteeId)).thenReturn(List.of(userDto));

        mockMvc.perform(get("/mentorships/mentors/{userId}", menteeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userDto.id()))
                .andExpect(jsonPath("$[0].username").value(userDto.username()));

        verify(service).getMentors(menteeId);
    }

    @Test
    void deleteMentorshipTest() throws Exception {
        doNothing().when(service).deleteMentorship(menteeId, mentorId);

        mockMvc.perform(delete("/mentorships/{menteeId}?mentorId={mentorId}", menteeId, mentorId))
                .andExpect(status().isOk());

        verify(service).deleteMentorship(menteeId, mentorId);
    }
}
