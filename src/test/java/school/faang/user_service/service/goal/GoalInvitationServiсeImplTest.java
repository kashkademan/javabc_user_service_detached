package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit тесты для GoalInvitationService")
class GoalInvitationServiceImplTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Spy
    private GoalInvitationMapper goalInvitationMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private GoalInvitationServiceImpl goalInvitationService;

    private User inviter;
    private User invited;
    private User anotherUser;
    private Goal goal;
    private GoalInvitation pendingInvitation;
    private GoalInvitation acceptedInvitation;
    private GoalInvitation rejectedInvitation;
    private CreateGoalInvitationDto createDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(goalInvitationService, "maxActiveGoals", 3);

        inviter = User.builder()
                .id(1L)
                .username("inviter")
                .email("inviter@test.com")
                .build();

        invited = User.builder()
                .id(2L)
                .username("invited")
                .email("invited@test.com")
                .build();

        anotherUser = User.builder()
                .id(3L)
                .username("another")
                .email("another@test.com")
                .build();

        goal = Goal.builder()
                .id(1L)
                .title("Test Goal")
                .users(new ArrayList<>())
                .mentor(null)
                .build();

        pendingInvitation = GoalInvitation.builder()
                .id(1L)
                .inviter(inviter)
                .invited(invited)
                .goal(goal)
                .status(RequestStatus.PENDING)
                .build();

        acceptedInvitation = GoalInvitation.builder()
                .id(2L)
                .inviter(inviter)
                .invited(anotherUser)
                .goal(goal)
                .status(RequestStatus.ACCEPTED)
                .build();

        rejectedInvitation = GoalInvitation.builder()
                .id(3L)
                .inviter(anotherUser)
                .invited(invited)
                .goal(goal)
                .status(RequestStatus.REJECTED)
                .build();

        createDto = new CreateGoalInvitationDto();
        createDto.setInvitedUserId(2L);
    }

    @Test
    @DisplayName("Должен успешно создать приглашение")
    void create_ShouldCreateInvitationSuccessfully() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invited));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalInvitationRepository.findAll()).thenReturn(List.of());
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(pendingInvitation);

        GoalInvitationDto expectedDto = new GoalInvitationDto();
        expectedDto.setId(1L);
        expectedDto.setStatus(RequestStatus.PENDING);

        doReturn(expectedDto).when(goalInvitationMapper).toGoalInvitationDto(any(GoalInvitation.class));

        GoalInvitationDto result = goalInvitationService.create(1L, createDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RequestStatus.PENDING, result.getStatus());

        verify(goalInvitationRepository).save(any(GoalInvitation.class));
        verify(goalInvitationMapper).toGoalInvitationDto(any(GoalInvitation.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение если отправитель не найден")
    void createShouldThrowException_WhenInviterNotFound() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> goalInvitationService.create(1L, createDto)
        );

        assertEquals("Inviter not found", exception.getMessage());
        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение если приглашаемый пользователь не найден")
    void createShouldThrowException_WhenInvitedUserNotFound() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> goalInvitationService.create(1L, createDto)
        );

        assertEquals("Invited user not found", exception.getMessage());
    }

    @Test
    @DisplayName("Должен выбросить исключение при попытке пригласить самого себя")
    void createShouldThrowException_WhenUserTriesToInviteThemselves() {
        createDto.setInvitedUserId(1L);
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> goalInvitationService.create(1L, createDto)
        );

        assertTrue(exception.getMessage().contains("Cannot invite yourself"));
        assertTrue(exception.getMessage().contains("User ID: 1"));
    }

    @Test
    @DisplayName("Должен выбросить исключение если пользователь уже участвует в цели")
    void createShouldThrowException_WhenUserAlreadyParticipating() {
        goal.getUsers().add(invited);
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invited));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> goalInvitationService.create(1L, createDto)
        );

        assertTrue(exception.getMessage().contains("User is already participating"));
    }

    @Test
    @DisplayName("Должен успешно принять приглашение")
    void acceptShouldAcceptInvitationSuccessfully() {
        when(userContext.getUserId()).thenReturn(2L);
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(pendingInvitation));
        when(goalRepository.countActiveGoalsPerUser(2L)).thenReturn(2);

        goalInvitationService.accept(1L);

        assertEquals(RequestStatus.ACCEPTED, pendingInvitation.getStatus());
        assertTrue(goal.getUsers().contains(invited));

        verify(goalInvitationRepository).save(pendingInvitation);
        verify(goalRepository).save(goal);
    }

    @Test
    @DisplayName("Должен выбросить исключение если приглашение не найдено")
    void acceptShouldThrowException_WhenInvitationNotFound() {
        when(userContext.getUserId()).thenReturn(2L);
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> goalInvitationService.accept(1L)
        );

        assertEquals("Invitation not found", exception.getMessage());
    }

    @Test
    @DisplayName("Должен выбросить ForbiddenException если пользователь пытается принять чужое приглашение")
    void acceptShouldThrowForbiddenException_WhenNotInvitationRecipient() {
        when(userContext.getUserId()).thenReturn(3L);
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(pendingInvitation));

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> goalInvitationService.accept(1L)
        );

        assertTrue(exception.getMessage().contains("You can only process invitations addressed to you"));
    }

    @Test
    @DisplayName("Должен выбросить исключение если у пользователя слишком много активных целей")
    void acceptShouldThrowException_WhenUserHasTooManyActiveGoals() {
        when(userContext.getUserId()).thenReturn(2L);
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(pendingInvitation));
        when(goalRepository.countActiveGoalsPerUser(2L)).thenReturn(3);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> goalInvitationService.accept(1L)
        );

        assertTrue(exception.getMessage().contains("You cannot have more than 3 active goals"));
    }

    @Test
    @DisplayName("Должен успешно отклонить приглашение")
    void rejectShouldRejectInvitationSuccessfully() {
        when(userContext.getUserId()).thenReturn(2L);
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(pendingInvitation));

        goalInvitationService.reject(1L);

        assertEquals(RequestStatus.REJECTED, pendingInvitation.getStatus());
        verify(goalInvitationRepository).save(pendingInvitation);
        verify(goalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен вернуть все приглашения без фильтров")
    void getByFilters_ShouldReturnAllInvitations_WhenNoFiltersApplied() {
        List<GoalInvitation> allInvitations = List.of(pendingInvitation, acceptedInvitation, rejectedInvitation);
        when(goalInvitationRepository.findAll()).thenReturn(allInvitations);

        GoalInvitationFilterDto filter = new GoalInvitationFilterDto(null, null, null);

        List<GoalInvitationDto> result = goalInvitationService.getByFilters(filter);

        assertEquals(3, result.size());
        verify(goalInvitationMapper, times(3)).toGoalInvitationDto(any());
    }

    @Test
    @DisplayName("Должен фильтровать по ID отправителя")
    void getByFilters_ShouldFilterByInviterId() {
        List<GoalInvitation> allInvitations = List.of(pendingInvitation, acceptedInvitation, rejectedInvitation);
        when(goalInvitationRepository.findAll()).thenReturn(allInvitations);

        GoalInvitationFilterDto filter = new GoalInvitationFilterDto(1L, null, null);

        List<GoalInvitationDto> result = goalInvitationService.getByFilters(filter);

        assertEquals(2, result.size()); // pendingInvitation and acceptedInvitation have inviter.id = 1L
        verify(goalInvitationMapper, times(2)).toGoalInvitationDto(any());
    }

    @Test
    @DisplayName("Должен фильтровать по ID получателя")
    void getByFilters_ShouldFilterByInvitedId() {
        List<GoalInvitation> allInvitations = List.of(pendingInvitation, acceptedInvitation, rejectedInvitation);
        when(goalInvitationRepository.findAll()).thenReturn(allInvitations);

        GoalInvitationFilterDto filter = new GoalInvitationFilterDto(null, 2L, null);

        List<GoalInvitationDto> result = goalInvitationService.getByFilters(filter);

        assertEquals(2, result.size()); // pendingInvitation and rejectedInvitation have invited.id = 2L
        verify(goalInvitationMapper, times(2)).toGoalInvitationDto(any());
    }

    @Test
    @DisplayName("Должен фильтровать по статусу PENDING")
    void getByFilters_ShouldFilterByStatus_Pending() {
        List<GoalInvitation> allInvitations = List.of(pendingInvitation, acceptedInvitation, rejectedInvitation);
        when(goalInvitationRepository.findAll()).thenReturn(allInvitations);

        GoalInvitationFilterDto filter = new GoalInvitationFilterDto(null, null, RequestStatus.PENDING);

        List<GoalInvitationDto> result = goalInvitationService.getByFilters(filter);

        assertEquals(1, result.size());
        verify(goalInvitationMapper, times(1)).toGoalInvitationDto(pendingInvitation);
    }

    @Test
    @DisplayName("Должен фильтровать по статусу ACCEPTED")
    void getByFilters_ShouldFilterByStatus_Accepted() {
        List<GoalInvitation> allInvitations = List.of(pendingInvitation, acceptedInvitation, rejectedInvitation);
        when(goalInvitationRepository.findAll()).thenReturn(allInvitations);

        GoalInvitationFilterDto filter = new GoalInvitationFilterDto(null, null, RequestStatus.ACCEPTED);

        List<GoalInvitationDto> result = goalInvitationService.getByFilters(filter);

        assertEquals(1, result.size());
        verify(goalInvitationMapper, times(1)).toGoalInvitationDto(acceptedInvitation);
    }

    @Test
    @DisplayName("Должен фильтровать по нескольким критериям")
    void getByFilters_ShouldFilterByMultipleCriteria() {
        List<GoalInvitation> allInvitations = List.of(pendingInvitation, acceptedInvitation, rejectedInvitation);
        when(goalInvitationRepository.findAll()).thenReturn(allInvitations);

        GoalInvitationFilterDto filter = new GoalInvitationFilterDto(1L, 2L, RequestStatus.PENDING);

        List<GoalInvitationDto> result = goalInvitationService.getByFilters(filter);

        assertEquals(1, result.size()); // Only pendingInvitation matches all criteria
        verify(goalInvitationMapper, times(1)).toGoalInvitationDto(pendingInvitation);
    }

    @Test
    @DisplayName("Должен вернуть пустой список если ничего не найдено")
    void getByFilters_ShouldReturnEmptyList_WhenNoInvitationsMatchFilter() {
        List<GoalInvitation> allInvitations = List.of(pendingInvitation, acceptedInvitation, rejectedInvitation);
        when(goalInvitationRepository.findAll()).thenReturn(allInvitations);

        GoalInvitationFilterDto filter = new GoalInvitationFilterDto(999L, null, null); // Non-existent inviter

        List<GoalInvitationDto> result = goalInvitationService.getByFilters(filter);

        assertEquals(0, result.size());
        verify(goalInvitationMapper, never()).toGoalInvitationDto(any());
    }

    @Test
    @DisplayName("Должен обрабатывать пустой список приглашений")
    void getByFilters_ShouldHandleEmptyInvitationList() {
        when(goalInvitationRepository.findAll()).thenReturn(List.of());
        GoalInvitationFilterDto filter = new GoalInvitationFilterDto(1L, 2L, RequestStatus.PENDING);

        List<GoalInvitationDto> result = goalInvitationService.getByFilters(filter);

        assertEquals(0, result.size());
        verify(goalInvitationMapper, never()).toGoalInvitationDto(any());
    }
}