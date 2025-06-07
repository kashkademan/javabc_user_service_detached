package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.GetMenteesResponseDto;
import school.faang.user_service.dto.mentorship.GetMentorsResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.mapper.mentorship.MentorMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;
    private final MentorMapper mentorsMapper;

    private User findById(long userId) {
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));
    }

    public List<GetMenteesResponseDto> getMentees(long userId) {
        List<User> mentees = findById(userId).getMentees();
        return mentees.stream()
                .map(menteeMapper::toDto)
                .toList();
    }

    public List<GetMentorsResponseDto> getMentors(long userId) {
        List<User> mentors = findById(userId).getMentors();
        return mentors.stream()
                .map(mentorsMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        deleteMentorAndMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        deleteMentorAndMentee(menteeId, mentorId);
    }

    private void deleteMentorAndMentee(long menteeId, long mentorId) {
        User mentee = findById(menteeId);
        User mentor = findById(mentorId);
        mentor.getMentees().remove(mentee);
        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentee);
        mentorshipRepository.save(mentor);
    }
}