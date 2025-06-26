package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final static int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;


    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill  already exists");
        }
        Skill entity = skillMapper.toEntity(skill);
        skillRepository.save(entity);
        return skillMapper.toDto(entity);
    }

    public List<SkillDto> getUserSkills(long usedId) {
        return skillRepository.findAllByUserId(usedId).stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(skillMapper::toDto, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User already has this skill");
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(userId, skillId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough offers to acquire this skill");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        List<UserSkillGuarantee> guarantees = offers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                        .user(new User(userId))
                        .skill(new Skill(userId))
                        .guarantor(new User(offer.getAuthorId()))
                        .build())
                .toList();
        userSkillGuaranteeRepository.saveAll(guarantees);

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not found"));
        return skillMapper.toDto(skill);
    }
}
