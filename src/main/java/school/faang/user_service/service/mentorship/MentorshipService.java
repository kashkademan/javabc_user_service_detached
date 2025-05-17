package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
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

import java.util.List;


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

        if (mentees.isEmpty()) {
            throw new IllegalArgumentException("У юзера нету учеников");
        }

        return mentees.stream()
                .map(MenteesMapper::toDto)
                .toList();
    }

    @Transactional
    public List<MentorDto> getMentors(long menteeId) {
        User mentee = findUserIntoUserRepository(menteeId);

        List<User> mentors = mentee.getMentors();

        return mentors.stream()
                .map(MentorsMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = findUserIntoMentorshipRepository(mentorId);
        User mentee = findUserIntoMentorshipRepository(menteeId);

        if (!mentor.getMentees().contains(mentee)) {
            throw new EntityNotFoundException("Пользователя нет в списке менти");
        }

        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    @Transactional
    public void deleteMentor (long mentorId, long menteeId) {
        User mentor = findUserIntoMentorshipRepository(mentorId);
        User mentee = findUserIntoMentorshipRepository(menteeId);

        if(!mentee.getMentors().contains(mentor)) {
            throw new EntityNotFoundException("Пользователя нету в списке менторов");
        }

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