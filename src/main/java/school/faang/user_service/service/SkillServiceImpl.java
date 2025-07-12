package school.faang.user_service.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final static long MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferService skillOfferService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillOfferMapper skillOfferMapper;

    @Override
    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill already exists.");
        }
        Skill savedSkill = skillRepository.save(skillMapper.toEntity(skill));
        return skillMapper.toDto(savedSkill);
    }

    @Override
    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
            .map(skillMapper::toDto)
            .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        Map<SkillDto, Long> skillCandidates = skills.stream()
            .collect(Collectors.groupingBy(
                skill -> skillMapper.toDto(skill),
                Collectors.counting()
            ));

        return skillCandidates.entrySet()
            .stream()
            .map(entry -> skillCandidateMapper.toDto(entry.getKey(), entry.getValue()))
            .toList();
    }

    @Transactional
    @Override
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User already has this skill.");
        }

        List<SkillOffer> skillOffers = skillOfferService.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            List<UserSkillGuarantee> userSkillGuarantees = skillOffers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                    .skill(offer.getSkill())
                    .guarantor(offer.getRecommendation().getAuthor())
                    .user(offer.getRecommendation().getReceiver())
                    .build()
                )
                .toList();
            userSkillGuaranteeService.saveAll(userSkillGuarantees);
        }

        return skillMapper.toDto(skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not foud.")));
    }

    @Override
    public List<SkillOfferDto> findAllOffersOfSkill(long skillId, long userId) {
        return skillOfferService.findAllOffersOfSkill(skillId, userId).stream()
            .map(skillOfferMapper::toDto)
            .toList();
    }
}
