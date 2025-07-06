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
    private int countOfSkillRecommendation;

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
if (skillRepository.existsByTitle(skillDto.title())){
    throw new IllegalArgumentException("Наименование навыка не может быть пустым");
}
        Skill skill = skillMapper.toSkill(skillDto);
        skill = skillRepository.save(skill);
        log.info("Skill {} created", skill.getTitle());
        return skillMapper.toSkillDto(skill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        List<Skill> skillList =skillRepository.findAllByUserId( userId);
        List<SkillDto>skillDtoList = new ArrayList<>();
        for (Skill skill : skillList){
            skillDtoList.add(skillMapper.toSkillDto(skill));
        }
        return skillDtoList;
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillList = skillRepository.findSkillsOfferedToUser(userId);
        List<SkillCandidateDto> skillCandidateDtoList = new ArrayList<>();
        for (Skill skill : skillList){
            int offersAmount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
            SkillCandidateDto skillCandidateDto = new SkillCandidateDto(skillMapper.toSkillDto(skill),offersAmount);
           skillCandidateDtoList.add(skillCandidateDto);
        }
        return skillCandidateDtoList;
    }

    @Override
    public void acquireSkillFromOffers(long skillId, long userId) {
        if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) < countOfSkillRecommendation) {
            throw new DataValidationException("Недостаточное колличество рекоммендаций навыка, добавление невозможно");
        }
        skillRepository.assignSkillToUser(skillId, userId);
    }
}
