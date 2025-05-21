package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;
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
    @Transactional(readOnly = true)
    public List<MenteeDto> getMentees(long userId) {
        User user = getUserById(userId);
        List<User> mentees = user.getMentees();
        if (mentees.isEmpty()) {
            return Collections.emptyList();
        }
        return mentees.stream()
                .map(menteeMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorDto> getMentors(long userId) {
        User user = getUserById(userId);
        List<User> mentors = user.getMentors();
        if (mentors.isEmpty()) {
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
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }
}