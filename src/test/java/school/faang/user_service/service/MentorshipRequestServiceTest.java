package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipResponseMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

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


        Mockito.when(mentorshipRequestRepository.create(, 5, "Это тестовое описание"))
                .thenReturn(new MentorshipRequest());

        mentorshipRequestService.requestMentorship();
    }
}
