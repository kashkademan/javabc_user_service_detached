package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Test
    @DisplayName("Запрос успешно создан")
    public void testRequestCreateSuccessfuly() {
        MentorshipRequest request = mentorshipRequestRepository.create(5, 6, "Тестовое описание");
        assertNotNull(request);
    }
}
