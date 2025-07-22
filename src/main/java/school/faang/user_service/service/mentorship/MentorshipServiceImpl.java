package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

/**
 * Класс имплементирющий интерфейс {@link MentorshipService} для управления зависимостями Менторов и подопечных.
 * <p>
 * Предоставляет методы для создания связи ментор с подопечными, получения данных и удалении связей.
 * </p>*
 *
 * <ul>
 *  *   <li>Создание связи</li>
 *  *   <li>Получение даныых о менторах и подопечных</li>
 *  *   <li>Удаление связей</li>
 *  * </ul>
 *
 * @author fomchenkoandrey
 */

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final MentorshipRepository mentorshipRepository;

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addMentorship(long mentorId, long menteeId) {
        validateMentorship(mentorId, menteeId);
        if (mentorshipRepository.existsById(mentorId) || mentorshipRepository.existsById(menteeId)) {
            throw new DataValidationException("Mentorship relationship already exists");
        }
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new DataValidationException("Mentor not found"));
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new DataValidationException("Mentee not found"));
        mentorshipRepository.save(mentee);
    }

    @Override
    @Transactional
    public List<UserDto> getMentees(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("User not found");
        }
        User mentor = mentorshipRepository.getByIdOrThrow(userId);
        List<User> menteesForMentor = mentor.getMentees();
        return menteesForMentor
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public List<UserDto> getMentors(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("User not found");
        }
        User mentee = mentorshipRepository.getByIdOrThrow(userId);
        List<User> mentorForMentees = mentee.getMentors();
        return mentorForMentees
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteMentorship(long menteeId, long mentorId) {
        validateMentorship(menteeId, mentorId);
        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);
        if (mentee.getMentors().contains(mentorId)) {
            mentee.getMentors().remove(mentorId);
            mentorshipRepository.save(mentee);
        } else {
            throw new DataValidationException("Mentor not found");
        }
    }


    private void validateMentorship(long mentorId, long menteeId) {
        if (mentorId == menteeId) {
            throw new DataValidationException("User cannot be a mentor for themselves");
        }
    }
}
