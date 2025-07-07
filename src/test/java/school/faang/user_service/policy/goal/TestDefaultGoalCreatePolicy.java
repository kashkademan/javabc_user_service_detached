package school.faang.user_service.policy.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TestDefaultGoalCreatePolicy {
    @InjectMocks
    DefaultGoalCreatePolicy policy;
    @Mock
    private UserContext userContext;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private GoalRepository goalRepository;

    @Test
    public void testValidateMenteeCreateGoalWithoutExceededLimit() {
        long mentorId = 1L;
        List<Long> menteeIds = List.of(1L);
        when(userContext.getUserId())
                .thenReturn(mentorId);
        when(mentorshipRepository.existsByMentorIdAndMenteeIds(mentorId, menteeIds))
                .thenReturn(true);
        when(goalRepository.countUsersExceedingGoals(eq(menteeIds), any(), anyLong()))
                .thenReturn((long) menteeIds.size());
        CreateGoalDto dto = new CreateGoalDto(
                null, null, null, mentorId, menteeIds, null
        );

        assertDoesNotThrow(() -> policy.validate(dto));
        verify(mentorshipRepository, times(1))
                .existsByMentorIdAndMenteeIds(mentorId, menteeIds);
        verify(goalRepository, times(1))
                .countUsersExceedingGoals(eq(menteeIds), any(), anyLong());
    }

    @Test
    public void testValidateMenteeCreateGoalWithExceededLimit() {
        long mentorId = 1L;
        List<Long> menteeIds = List.of(1L);
        when(userContext.getUserId())
                .thenReturn(mentorId);
        when(mentorshipRepository.existsByMentorIdAndMenteeIds(mentorId, menteeIds))
                .thenReturn(true);
        when(goalRepository.countUsersExceedingGoals(eq(menteeIds), any(), anyLong()))
                .thenReturn(0L);
        CreateGoalDto dto = new CreateGoalDto(
                null, null, null, mentorId, menteeIds, null
        );

        assertThrows(IllegalArgumentException.class, () -> policy.validate(dto));
        verify(mentorshipRepository, times(1))
                .existsByMentorIdAndMenteeIds(mentorId, menteeIds);
        verify(goalRepository, times(1))
                .countUsersExceedingGoals(eq(menteeIds), any(), anyLong());
    }

    @Test
    public void testValidateSelfCreateGoal() {
        long userId = 1L;
        List<Long> menteeIds = List.of(userId);
        CreateGoalDto dto = new CreateGoalDto(
                null, null, null, null, menteeIds, null
        );
        when(userContext.getUserId())
                .thenReturn(userId);
        when(goalRepository.countUsersExceedingGoals(eq(menteeIds), any(), anyLong()))
                .thenReturn((long) menteeIds.size());

        assertDoesNotThrow(() -> policy.validate(dto));
        verify(mentorshipRepository, times(0))
                .existsByMentorIdAndMenteeIds(anyLong(), anyList());
    }

    @Test
    public void testValidateNotSelfAndNotMenteeCreateGoal() {
        long userId = 1L;
        List<Long> menteeIds = List.of(2L);
        CreateGoalDto dto = new CreateGoalDto(
                null, null, null, 3L, menteeIds, null
        );
        when(userContext.getUserId())
                .thenReturn(userId);

        assertThrows(IllegalArgumentException.class, () -> policy.validate(dto));
        verify(mentorshipRepository, times(1))
                .existsByMentorIdAndMenteeIds(anyLong(), anyList());
    }
}

