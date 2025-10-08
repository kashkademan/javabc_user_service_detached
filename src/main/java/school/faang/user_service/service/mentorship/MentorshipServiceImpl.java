package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validation.mentorship.MentorshipValidation;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipValidation mentorshipValidation;
    private final UserMapper userMapper;
    private final UserContext userContext;

    @Override
    public void addMentorship(long mentorId, long menteeId) {
        if (!mentorshipValidation.canAddMentorship(mentorId,
                menteeId,
                (mentor, mentee) -> !Objects.equals(mentor, mentee))) {
            throw new DataValidationException("Mentor Id and Mentee id can't be equal");
        }

        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);
        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);

        if (mentee.getMentors().contains(mentor)) {
            log.info("Mentee {} already has mentor {}", menteeId, mentorId);
            return;
        }

        mentee.getMentors().add(mentor);
        mentorshipRepository.save(mentee);

        log.info("Mentorship created between mentor {} and mentee {}", mentorId, menteeId);
    }

    @Override
    public List<UserDto> getMentees(long userId) {
        User mentor = mentorshipRepository.getByIdOrThrow(userId);
        return mentor.getMentees()
                .stream()
                .map(userMapper::toUserDto)
                .toList();

    }

    @Override
    public List<UserDto> getMentors(long userId) {
        User mentee = mentorshipRepository.getByIdOrThrow(userId);
        return mentee.getMentors()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteMentorship(long menteeId, long mentorId) {
        long currentUserId = userContext.getUserId();

        if (currentUserId != menteeId && currentUserId != mentorId) {
            throw new ForbiddenException("You are not a participant in the deleting mentorship!");
        }

        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);
        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);
        mentee.getMentors().remove(mentor);

        mentorshipRepository.save(mentee);
    }

}
