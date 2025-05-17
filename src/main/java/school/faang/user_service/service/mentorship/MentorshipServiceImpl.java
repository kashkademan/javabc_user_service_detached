package school.faang.user_service.service.mentorship;

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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeMapper menteeMapper;
    private final MentorMapper mentorMapper;

    public List<MenteeDto> getMentees(long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found."));
        return Optional.ofNullable(user.getMentees())
                .orElse(Collections.emptyList())
                .stream()
                .map(menteeMapper::toDto)
                .toList();
    }

    public List<MentorDto> getMentors(long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found."));
        return Optional.ofNullable(user.getMentors())
                .orElse(Collections.emptyList())
                .stream()
                .map(mentorMapper::toDto)
                .toList();
    }
}