package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    public static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillValidator skillValidator;
    private final UserValidator userValidator;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public SkillDto create(SkillDto skillDto) {
        skillValidator.validatorTitleSkill(skillDto);
        Skill skill = skillMapper.toEntity(skillDto);
        Skill result = skillRepository.save(skill);
        return skillMapper.toDto(result);
    }

    public List<SkillDto> getUserSkills(long userId) {
        userValidator.validatorUserExistence(userId);
        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        userValidator.validatorUserExistence(userId);
        Map<Long, Long> guardianOfferedSkills = skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(Skill::getId, Collectors.counting()));

        return guardianOfferedSkills.entrySet().stream()
                .map(entry -> skillRepository.findById(entry.getKey())
                        .map(skill -> skillCandidateMapper.toDto(skillMapper.toDto(skill), entry.getValue())))
                .flatMap(Optional::stream)
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        userValidator.validatorUserExistence(userId);
        skillValidator.validatorSkillId(skillId);
        Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillId, userId);
        List<SkillOffer> guaranteeUser = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (optionalSkill.isPresent()) {
            return skillMapper.toDto(optionalSkill.get());
        }
        skillValidator.validatorSkillOfferIsEmpty(guaranteeUser);
        skillValidator.checkValidRecommendation(guaranteeUser);

        skillRepository.assignSkillToUser(skillId, userId);
        saveSkillGuarantees(guaranteeUser);
        return skillMapper.toDto(skillRepository.getReferenceById(skillId));
    }

    private void saveSkillGuarantees(List<SkillOffer> guaranteeUser) {
        List<UserSkillGuarantee> guarantees = guaranteeUser.stream()
                .map(skillOffer -> UserSkillGuarantee.builder()
                        .user(skillOffer.getRecommendation().getReceiver())
                        .skill(skillOffer.getSkill())
                        .guarantor(skillOffer.getRecommendation().getAuthor())
                        .build())
                .collect(Collectors.toList());

        userSkillGuaranteeRepository.saveAll(guarantees);
    }
}