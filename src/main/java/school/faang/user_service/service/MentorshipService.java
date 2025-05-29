package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        userValidator.validatorUserExistence(userId);
        List<User> menteesList = mentorshipRepository.findById(userId)
                .map(User::getMentees)
                .orElse(Collections.emptyList());

        return userMapper.toDtoList(menteesList);
    }

    public List<UserDto> getMentors(long userId) {
        userValidator.validatorUserExistence(userId);
        List<User> mentorsList = mentorshipRepository.findById(userId)
                .map(User::getMentors)
                .orElse(Collections.emptyList());

        return userMapper.toDtoList(mentorsList);
    }

    @Transactional
    public ResponseEntity<Void> deleteMentee(long menteeId, long mentorId) {
        userValidator.validatorUserExistence(menteeId);
        userValidator.validatorUserExistence(mentorId);
        User mentee = getUserOrThrow(menteeId, "Подопечный не найден!");
        User mentor = getUserOrThrow(mentorId, "Ментор не найден!");

        if (mentor.getMentees().isEmpty()) {
            throw new DataValidationException("Ваш список подопечных пуст!");
        }
        if (removeMentees(mentor, menteeId) && removeMentor(mentee, mentorId)) {
            mentorshipRepository.save(mentee);
            mentorshipRepository.save(mentor);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public ResponseEntity<Void> deleteMentor(long menteeId, long mentorId) {
        userValidator.validatorUserExistence(menteeId);
        userValidator.validatorUserExistence(mentorId);
        User mentee = getUserOrThrow(menteeId, "Подопечный не найден!");
        User mentor = getUserOrThrow(mentorId, "Ментор не найден!");

        if (mentee.getMentors().isEmpty()) {
            throw new DataValidationException("У вас нет наставников!");
        }
        if (removeMentees(mentor, menteeId) && removeMentor(mentee, mentorId)) {
            mentorshipRepository.save(mentee);
            mentorshipRepository.save(mentor);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private User getUserOrThrow(long userId, String errorMessage) {
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException(errorMessage));
    }

    private boolean removeMentees(User mentor, long menteeId) {
        return mentor.getMentees().removeIf(user -> user.getId().equals(menteeId));
    }

    private boolean removeMentor(User mentee, long mentorId) {
        return mentee.getMentors().removeIf(user -> user.getId().equals(mentorId));
    }
}