package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;


    @Transactional
    @Override
    public void addMentorship(Long mentorId, Long menteeId) {
        checkDuplicateMentorship(mentorId, menteeId);

        if (mentorshipRepository.existsMentorship(mentorId, menteeId)) {
            log.warn("Связь между mentor {} и mentee {} уже существует", mentorId, menteeId);
            throw new DataValidationException("Связь уже существует");
        }

        mentorshipRepository.addMentorshipNative(mentorId, menteeId);
    }

    @Override
    public List<UserDto> getMentees(Long userId) {
        return mentorshipRepository.getMenteesById(userId).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getMentors(Long userId) {
        return mentorshipRepository.getMentorsById(userId).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteMentorship(Long mentorId, Long menteeId) {
        Long currentUserId = userContext.getUserId();
        if (!currentUserId.equals(menteeId) && !currentUserId.equals(mentorId)) {
            throw new ForbiddenException("Недостаточно прав");
        }

        checkDuplicateMentorship(menteeId, mentorId);

        mentorshipRepository.deleteMentorshipNative(mentorId, menteeId);
    }

    private void checkDuplicateMentorship(Long menteeId, Long mentorId) {
        if (mentorId.equals(menteeId)) {
            log.warn("Дубликат ключа mentorId и menteeId");
            throw new DataValidationException("Переданы дублирующиеся значения");
        }
    }
}
