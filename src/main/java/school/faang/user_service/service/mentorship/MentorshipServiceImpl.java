package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MenteeMapper;
import school.faang.user_service.mapper.MentorMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;
    private final MentorMapper mentorMapper;

    @Override
    @Transactional
    public List<MenteeDto> getMentees(long userId) {
        User user = getUserById(userId);
        List<User> mentees = user.getMentees();
        if (mentees == null || mentees.isEmpty()) {
            return Collections.emptyList();
        }
        return mentees.stream()
                .map(menteeMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<MentorDto> getMentors(long userId) {
        User user = getUserById(userId);
        List<User> mentors = user.getMentees();
        if (mentors == null || mentors.isEmpty()) {
            return Collections.emptyList();
        }
        return mentors.stream()
                .map(mentorMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteMentorship(long mentorId, long menteeId) {
        User mentor = getUserById(mentorId);
        User mentee = getUserById(menteeId);
        mentor.getMentees().removeIf(user -> user.getId().equals(menteeId));
        mentee.getMentors().removeIf(user -> user.getId().equals(mentorId));
        mentorshipRepository.save(mentor);
        mentorshipRepository.save(mentee);
    }

    private User getUserById(long userId) {
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found."));
    }
}