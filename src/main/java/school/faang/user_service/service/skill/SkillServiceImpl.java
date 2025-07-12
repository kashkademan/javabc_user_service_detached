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

    private final SkillRepository repository;
    private final SkillMapper mapper;
    private final SkillOfferRepository offerRepository;
    private final SkillServiceValidator serviceValidator;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        serviceValidator.validationByNameSkillInTheDataBase(skillDto.title());
        Skill skill = mapper.toEntity(skillDto);
        skill = repository.save(skill);
        log.info("Skill {} created", skill.getTitle());
        return mapper.toViewDto(skill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        List<Skill> skillList = repository.findAllByUserId(userId);
        serviceValidator.validateNotNull(skillList,
                "there are no skills with such a user id: " + userId + " in the database");
        return skillList.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> skillList = repository.findSkillsOfferedToUser(userId);
        serviceValidator.validateNotNull(skillList,
                "there are no skills offered to user with such a user id: " + userId + " in the database");
        List<SkillCandidateDto> skillCandidateDtoList = new ArrayList<>();
        for (Skill skill : skillList) {
            int offersAmount = offerRepository.countAllOffersOfSkill(skill.getId(), userId);
            SkillCandidateDto skillCandidateDto = new SkillCandidateDto(mapper.toViewDto(skill), offersAmount);
            skillCandidateDtoList.add(skillCandidateDto);
        }
        return skillCandidateDtoList;
    }

    @Override
    public void acquireSkillFromOffers(Long skillId, Long userId) {
        serviceValidator.validationCountOfferOfSkill(skillId, userId, countOfSkillRecommendation);
        serviceValidator.validationSkillOfUser(skillId, userId);
        repository.assignSkillToUser(skillId, userId);
    }
}
