package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    @Value("${user.skill.recommendation.min}")
    private int minSkillRecommendationsCount;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserContext userContext;
    private final UserRepository userRepository;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        if (skillDto == null) {
            throw new DataValidationException("CreateSkillDto cannot be null");
        }
        if (skillDto.title() == null || skillDto.title().isBlank()) {
            throw new DataValidationException("The Title field cannot be null or empty");
        }
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new DataValidationException("This skill already exists.");
        }

        Skill skill = skillMapper.toSkill(skillDto);
        Skill saveToDto = skillRepository.save(skill);
        log.info("Skill {} saved to DB", skillDto.title());

        return skillMapper.toSkillDto(saveToDto);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        validateUserId(userId);
        validateCurrentUserMatches(userId);

        List<Skill> skills = skillRepository.findAllByUserId(userId);

        if (skills.isEmpty()) {
            log.info("User with ID {} has no skills assigned", userId);
            return Collections.emptyList();
        }

        log.info("The skills of the user with the id= {} have been obtained", userId);

        return skills.stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);

        if (offeredSkills.isEmpty()) {
            log.info("The user with the ID {} has not been offered any skills", userId);
            return Collections.emptyList();
        }

        List<SkillCandidateDto> result = offeredSkills.stream()
                .map(skill -> {
                    SkillDto skillDto = skillMapper.toSkillDto(skill);
                    int offersCount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
                    return new SkillCandidateDto(skillDto, offersCount);
                })
                .collect(Collectors.toList());

        log.info("Successfully received data on {} suggested skills for the user {}",
                result.size(), userId);

        return result;
    }

    @Override
    public void acquireSkillFromOffers(long skillId, Long userId) {
        validateSkillExists(skillId);
        validateUserExists(userId);
        validateCurrentUserMatches(userId);
        validateUserDoesNotHaveSkill(skillId, userId);
        validateSkillWasOffered(skillId, userId);
        validateRecommendationsCount(skillId, userId, minSkillRecommendationsCount);

        log.info("User {} acquires a skill {}", userId, skillId);
        skillRepository.assignSkillToUser(skillId, userId);
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId < 0) {
            throw new DataValidationException("ID user cannot be null or less than zero");
        }
    }

    private void validateCurrentUserMatches(Long userId) {
        if (!userId.equals(userContext.getUserId())) {
            throw new DataValidationException("You can not perform actions on behalf of another user");
        }
    }

    private Skill validateSkillExists(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Skill with ID " + skillId + " was not found"));
    }

    private User validateUserExists(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User with ID " + userId + " not found "));
    }

    private void validateUserDoesNotHaveSkill(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("You already have this skill");
        }
    }

    private void validateSkillWasOffered(long skillId, long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        boolean isOffered = offeredSkills.stream().anyMatch(s -> s.getId() == skillId);
        if (!isOffered) {
            throw new DataValidationException("This skill was not offered to you");
        }
    }

    private void validateRecommendationsCount(long skillId, long userId, int minCount) {
        int recommendationsCount = skillOfferRepository.countAllOffersOfSkill(skillId, userId);
        if (recommendationsCount < minCount) {
            throw new DataValidationException("At least " + minCount
                    + " you need to assign a skill Recommendations, you now have:" + recommendationsCount);
        }
    }
}