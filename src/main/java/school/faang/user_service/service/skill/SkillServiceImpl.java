package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    @Value("${skill.count-recommendation.min-length}")
    private static int countOfSkillRecommendation;

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillServiceValidator skillServiceValidator;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        skillServiceValidator.validationByNameSkillInTheDataBase(skillDto.title());
        Skill skill = skillMapper.toSkill(skillDto);
        skill = skillRepository.save(skill);
        log.info("Skill {} created", skill.getTitle());
        return skillMapper.toSkillDto(skill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        List<Skill> skillList = skillRepository.findAllByUserId(userId);
        skillServiceValidator.validateNotNull(skillList,
                "there are no skills with such a user id: " + userId + " in the database");
        return skillList.stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillList = skillRepository.findSkillsOfferedToUser(userId);
        skillServiceValidator.validateNotNull(skillList,
                "there are no skills offered to user with such a user id: " + userId + " in the database");
        List<SkillCandidateDto> skillCandidateDtoList = new ArrayList<>();
        for (Skill skill : skillList) {
            int offersAmount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
            SkillCandidateDto skillCandidateDto = new SkillCandidateDto(skillMapper.toSkillDto(skill), offersAmount);
            skillCandidateDtoList.add(skillCandidateDto);
        }
        return skillCandidateDtoList;
    }

    @Override
    public void acquireSkillFromOffers(long skillId, long userId) {
        skillServiceValidator.validationCountOfferOfSkill(skillId, userId, countOfSkillRecommendation);
        skillServiceValidator.validationSkillOfUser(skillId, userId);
        skillRepository.assignSkillToUser(skillId, userId);
    }
}
