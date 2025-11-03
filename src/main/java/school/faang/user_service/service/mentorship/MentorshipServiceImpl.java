package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.repository.mentorship.service.validation.MentorshipServiceValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Component
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final MentorshipServiceValidation validation;

    @Override
    public MentorshipDto addMentorship(long mentorId, long menteeId) {
        long currentUserId = userContext.getUserId();

        validation.validationCurrentUser(currentUserId, mentorId, menteeId);
        validation.validationIds(mentorId, menteeId);

        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);
        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);

        if (mentee.getMentors().contains(mentor)) {
            log.info("Mentor {} is already mentor for mentee {}", mentorId, menteeId);
        }

        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);

        mentorshipRepository.save(mentee);
        mentorshipRepository.save(mentor);
        log.info("Mentor: {} added to mentee: {}", mentorId, menteeId);
        return createMentorshipDto(mentorId, menteeId);
    }

    @Override
    public List<UserDto> getMentees(long userId) {
        User mentor = mentorshipRepository.getByIdOrThrow(userId);

        List<User> menteeList = new ArrayList<>(mentor.getMentees());
        List<UserDto> menteeDtoList = new ArrayList<>();

        if (!menteeList.isEmpty()) {
            for (User mentee : menteeList) {
                menteeDtoList.add(userMapper.toUserDto(mentee));
            }
            log.info("Mentee Dto List formed: {}", menteeDtoList);
        }
        return menteeDtoList;
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        User mentee = mentorshipRepository.getByIdOrThrow(userId);

        List<User> mentorList = new ArrayList<>(mentee.getMentors());
        List<UserDto> mentorDtoList = new ArrayList<>();

        for (User mentor : mentorList) {
            mentorDtoList.add(userMapper.toUserDto(mentor));
        }
        log.info("Mentor Dto List formed: {}", mentorDtoList);
        return mentorDtoList;
    }

    @Override
    public void deleteMentorship(long menteeId, long mentorId) {
        long currentUserId = userContext.getUserId();

        validation.validationCurrentUser(currentUserId, mentorId, menteeId);
        validation.validationIds(mentorId, menteeId);

        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);
        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);

        boolean removedFromMentee = mentee.getMentors().removeIf(m -> m.getId().equals(mentorId));
        boolean removedFromMentor = mentor.getMentees().removeIf(m -> m.getId().equals(menteeId));

        if (removedFromMentee && removedFromMentor) {
            mentorshipRepository.save(mentee);
            mentorshipRepository.save(mentor);
            log.info("Mentorship deleted: mentor {} removed from mentee {}", mentorId, menteeId);
        } else {
            log.info("Mentorship not found between mentor {} and mentee {}", mentorId, menteeId);
        }
    }

    private MentorshipDto createMentorshipDto(long mentorId, long menteeId) {
        return MentorshipDto.builder()
                .mentorId(mentorId)
                .menteeId(menteeId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
