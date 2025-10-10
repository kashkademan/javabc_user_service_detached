package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final int countOfOffers = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        Skill skill = skillMapper.toSkill(skillDto);
        if (!skillRepository.existsByTitle(skillDto.getTitle())) {
            skill = skillRepository.save(skill);
            return skillMapper.toSkillDto(skill);
        } else {
            throw new DataValidationException("skill title should not be empty");
        }

    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        return skillMapper.toSkillsDto(skillRepository.findAllByUserId(userId));
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .map(skill -> {
                    SkillDto skillDto = skillMapper.toSkillDto(skill);
                    int offersAmount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
                    return new SkillCandidateDto(skillDto, offersAmount);
                }).toList();
    }

    @Override
    public void acquireSkillFromOffers(long skillId, long userId) {
        if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) > countOfOffers) {
            skillRepository.assignSkillToUser(skillId, userId);
        }
    }
}
