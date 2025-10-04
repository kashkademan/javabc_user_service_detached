package school.faang.user_service.service.skill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SkillServiceImpl implements SkillService {

    private final int minSkillRecommendationsCount;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserContext userContext;
    private final UserRepository userRepository;

    @Autowired
    public SkillServiceImpl(@Value("${user.skill.recommendation.min}") int minSkillRecommendationsCount,
                            SkillRepository skillRepository,
                            SkillMapper skillMapper,
                            SkillOfferRepository skillOfferRepository,
                            UserContext userContext,
                            UserRepository userRepository) {
        this.minSkillRecommendationsCount = minSkillRecommendationsCount;
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.skillOfferRepository = skillOfferRepository;
        this.userContext = userContext;
        this.userRepository = userRepository;
    }

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new DataValidationException("Такой скилл уже существует");
        }

        Skill skill = skillMapper.toSkill(skillDto);
        Skill saveToDto = skillRepository.save(skill);
        log.info("Скилл '{}' успешно создан", skillDto.title());

        return skillMapper.toSkillDto(saveToDto);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        if (skills.isEmpty()) {
            log.info("Пользователю с ID {} не назначено ни одного скилла", userId);
            return Collections.emptyList();
        }

        log.info("Получены скиллы пользователя с id= {}", userId);

        return skills.stream()
                .map(skill -> skillMapper.toSkillDto(skill))
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);

        if (offeredSkills.isEmpty()) {
            log.info("Пользователю с ID {} не предложено ни одного скилла", userId);
            return Collections.emptyList();
        }

        List<SkillCandidateDto> result = offeredSkills.stream()
                .map(skill -> {
                    SkillDto skillDto = skillMapper.toSkillDto(skill);
                    int offersCount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
                    return new SkillCandidateDto(skillDto, offersCount);
                })
                .collect(Collectors.toList());

        log.info("Успешно получены данные о {} предложенных скиллах для пользователя {}",
                result.size(), userId);

        return result;
    }

    @Override
    public void acquireSkillFromOffers(long skillId, long userId) {
        validateSkillExists(skillId);
        validateUserExists(userId);
        validateUserAuthorization(userId);
        validateUserDoesNotHaveSkill(skillId, userId);
        validateSkillWasOffered(skillId, userId);
        validateRecommendationsCount(skillId, userId, minSkillRecommendationsCount);

        log.info("Пользователь {} приобретает скилл {}", userId, skillId);
        skillRepository.assignSkillToUser(skillId, userId);
    }

    private Skill validateSkillExists(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Скилл с ID " + skillId + " не найден"));
    }

    private User validateUserExists(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private void validateUserAuthorization(long userId) {
        if (userContext.getUserId() != userId) {
            throw new ForbiddenException("Нельзя приобретать скиллы для других пользователей");
        }
    }

    private void validateUserDoesNotHaveSkill(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("У вас уже есть этот скилл");
        }
    }

    private void validateSkillWasOffered(long skillId, long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        boolean isOffered = offeredSkills.stream().anyMatch(s -> s.getId() == skillId);
        if (!isOffered) {
            throw new DataValidationException("Этот скилл не был вам предложен");
        }
    }

    private void validateRecommendationsCount(long skillId, long userId, int minCount) {
        int recommendationsCount = skillOfferRepository.countAllOffersOfSkill(skillId, userId);
        if (recommendationsCount < minCount) {
            throw new DataValidationException("Для присвоения скилла необходимо минимум " + minCount
                    + " рекомендации, у вас сейчас: " + recommendationsCount);
        }
    }
}