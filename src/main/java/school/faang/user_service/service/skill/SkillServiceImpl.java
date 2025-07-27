package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillViewDto;
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
    private int countOfSkillRecommendation;

    private final SkillRepository repository;
    private final SkillMapper mapper;
    private final SkillOfferRepository offerRepository;
    private final SkillServiceValidator validator;

    @Override
    public SkillViewDto create(SkillCreateDto skillDto) {
        validator.validationByNameSkillInTheDataBase(skillDto.title());
        Skill skill = mapper.toEntity(skillDto);
        skill = repository.save(skill);
        log.info("Skill {} created", skill.getTitle());
        return mapper.toViewDto(skill);
    }

    @Override
    public List<SkillViewDto> getByUserId(Long userId) {
        List<Skill> skillList = repository.findAllByUserId(userId);
        validator.validateNotNull(skillList,
                "there are no skills with such a user id: " + userId + " in the database");
        return skillList.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    public List<SkillOfferDto> getOfferedSkills(Long userId) {
        List<Skill> skillList = repository.findSkillsOfferedToUser(userId);
        validator.validateNotNull(skillList,
                "there are no skills offered to user with such a user id: " + userId + " in the database");
        List<SkillOfferDto> skillCandidateDtoList = new ArrayList<>();
        for (Skill skill : skillList) {
            int offersAmount = offerRepository.countAllOffersOfSkill(skill.getId(), userId);
            SkillOfferDto skillCandidateDto = new SkillOfferDto(mapper.toViewDto(skill), offersAmount);
            skillCandidateDtoList.add(skillCandidateDto);
        }
        return skillCandidateDtoList;
    }

    @Override
    public void acquireSkillFromOffers(Long skillId, Long userId) {
        validator.validationCountOfferOfSkill(skillId, userId, countOfSkillRecommendation);
        validator.validationSkillOfUser(skillId, userId);
        repository.assignSkillToUser(skillId, userId);
        log.info("Skill [ID={}] is useful to the user [ID={}]", skillId, userId);
    }
}
