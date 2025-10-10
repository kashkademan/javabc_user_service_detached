package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;

    @Override
    public void create(CreateSkillDto skillDto) {
        if (!skillRepository.existsByTitle(skillDto.getTitle())) {
            skillRepository.save(skillMapper.toSkill(skillDto));
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
        if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) > 3) {
            skillRepository.assignSkillToUser(skillId, userId);
        }
    }
}
