package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.dto.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteesMapper;
import school.faang.user_service.mapper.MentorsMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.volidation.mentorship.MentorshipValidation;

import java.util.List;

import static school.faang.user_service.volidation.mentorship.MentorshipValidation.validateIsMenteeOf;
import static school.faang.user_service.volidation.mentorship.MentorshipValidation.validateIsMentorOf;
import static school.faang.user_service.volidation.mentorship.MentorshipValidation.validateMentorshipUsers;


@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<MenteeDto> getMentees(long mentorId) {
        User mentor = findUserIntoUserRepository(mentorId);
        List<User> mentees = mentor.getMentees();

        MentorshipValidation.validateMenteesNonEmpty(mentees);

        return mentees.stream()
                .map(MenteesMapper::toDto)
                .toList();
    }

    @Transactional
    public List<MentorDto> getMentors(long menteeId) {
        User mentee = findUserIntoUserRepository(menteeId);
        List<User> mentors = mentee.getMentors();

        MentorshipValidation.validateMentorsNonEmpty(mentors);

        return mentors.stream()
                .map(MentorsMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = findUserIntoMentorshipRepository(mentorId);
        User mentee = findUserIntoMentorshipRepository(menteeId);

        validateMentorshipUsers(mentor, mentee);
        validateIsMenteeOf(mentor, mentee);

        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    @Transactional
    public void deleteMentor(long mentorId, long menteeId) {
        User mentor = findUserIntoMentorshipRepository(mentorId);
        User mentee = findUserIntoMentorshipRepository(menteeId);

        validateMentorshipUsers(mentor, mentee);
        validateIsMentorOf(mentor, mentee);

        mentee.getMentors().remove(mentor);
        userRepository.save(mentor);
    }

    private User findUserIntoMentorshipRepository(long userId) {
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Такого пользователя нет в базе"));
    }

    private User findUserIntoUserRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Такого пользователя нет в базе"));
    }
}