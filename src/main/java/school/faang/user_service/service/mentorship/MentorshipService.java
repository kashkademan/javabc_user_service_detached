package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validation.mentorship.MentorshipValidation;

import java.util.List;

import static school.faang.user_service.validation.mentorship.MentorshipValidation.validateIsMenteeOf;
import static school.faang.user_service.validation.mentorship.MentorshipValidation.validateIsMentorOf;
import static school.faang.user_service.validation.mentorship.MentorshipValidation.validateMentorshipUsers;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getMentees(long mentorId) {
        User mentor = findUserIntoUserRepository(mentorId);
        List<User> mentees = mentor.getMentees();
        MentorshipValidation.validateListUsersNotNull(mentees);
        return mentees;
    }

    @Transactional(readOnly = true)
    public List<User> getMentors(long menteeId) {
        User mentee = findUserIntoUserRepository(menteeId);
        List<User> mentors = mentee.getMentors();
        MentorshipValidation.validateListUsersNotNull(mentors);
        return mentors;
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
        userRepository.save(mentee);
    }

    private User findUserIntoMentorshipRepository(long userId) {
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist in the database"));
    }

    private User findUserIntoUserRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist in the database"));
    }
}