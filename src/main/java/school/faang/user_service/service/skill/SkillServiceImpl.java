package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    @Value("skill.offers.min.count")
    private int MIN_COUNT_OFFERS;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        Skill skill = skillMapper.toSkill(skillDto);
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new ForbiddenException("this skill already exists");
        }
        log.info("create skill {}", skillDto.getTitle());
        skill = skillRepository.save(skill);
        return skillMapper.toSkillDto(skill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        log.info("get list skills from user {}", userId);
        return skillMapper.toSkillsDto(skillRepository.findAllByUserId(userId));
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        log.info("get offered skills from user {}", userId);
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .map(skill -> {
                    SkillDto skillDto = skillMapper.toSkillDto(skill);
                    int offersAmount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
                    return new SkillCandidateDto(skillDto, offersAmount);
                }).toList();
    }

    @Override
    public void acquireSkillFromOffers(long skillId, long userId) {
        if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) < MIN_COUNT_OFFERS) {
            throw new ForbiddenException("Offers count should be more than " + MIN_COUNT_OFFERS);
        }
        log.info("user {} acquire skill {}", userId, skillId);
        skillRepository.assignSkillToUser(skillId, userId);
    }
}
