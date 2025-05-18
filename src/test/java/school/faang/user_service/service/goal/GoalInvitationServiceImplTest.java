package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.filter.invitation.InvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceImplTest {

    GoalInvitationServiceImpl service;

    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;
    private List<InvitationFilter> invitationFilters;

    @BeforeEach
    private void setup() {
        service = new GoalInvitationServiceImpl(
                goalInvitationRepository,
                goalRepository,
                userRepository,
                goalInvitationMapper,
                invitationFilters
        );
        ReflectionTestUtils.setField(service, "maximumAllowedActiveGoals", 3);
    }

    @Test
    void createInvitation() {
    }

    //тест на эксепшен при inviterId = invitedId
    //тест на успешное создание - вызван маппер,
    // определены поля Goal, Inviter, Invited через обращение к goal repository и userRepository,
    // что у созданной цели статус пендинг
    // что приглашение было сохранено в репозиторий

    //accept
//    NoSuchElementException("Invitation ID: " + id));
//    IllegalStateException("No existing goal in invitation");
//    UnsupportedOperationException("Invited user already works on goal");
//    DataValidationException("User has Maximum allowed active goals");
    //happyPath for void: Accepted, savedandflush for invitationRepo, userRepo, goalRepo


    //reject
//    NoSuchElementException("Invitation ID: " + id));
    //happyPath: statusRejected, goalInvitationRepo.savedAndFlushed


    //getInvitations
    //Здесь нужно получить список всех приглашений, а затем отфильтровать.

}