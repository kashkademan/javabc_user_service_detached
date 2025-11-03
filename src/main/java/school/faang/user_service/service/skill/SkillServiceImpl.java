package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public SkillDto create(CreateSkillDto createSkillDto) {
        if (skillRepository.existsByTitle(createSkillDto.title())) {
            log.error("Скилл с названием {} уже существует.", createSkillDto.title());
            throw new DataValidationException("Скилл с таким названием уже существует.");
        }
        Skill createdSkill = skillRepository.save(skillMapper.toSkill(createSkillDto));
        log.info("Скилл с названием {} успешно создан.", createSkillDto.title());
        return skillMapper.toSkillDto(createdSkill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        List<Skill> allUserSkills = skillRepository.findAllByUserId(userId);
        List<SkillDto> allUserSkillDtos = new ArrayList<>();
        for (Skill skill : allUserSkills) {
            List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skill.getId(), userId);
            List<UserDto> garantors = new ArrayList<>();
            for (int i = 0; i < skillOffers.size(); i++) {
                garantors.add(userMapper.toUserDto(skillOffers
                                .get(i)
                                .getRecommendation()
                                .getAuthor()));
            }
            allUserSkillDtos.add(skillMapper.toSkillDto(skill));
        }
        return allUserSkillDtos;
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        List<SkillCandidateDto> skillCandidateDtos = new ArrayList<>();
        if (offeredSkills.isEmpty()) {
            log.warn("У пользователя с id: {} нет никаких предложений скиллов.", userId);
            throw new EntityNotFoundException("Нет никаких скиллов.");
        }
        for (Skill skill : offeredSkills) {
            long offeresAmount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
            skillCandidateDtos.add(new SkillCandidateDto(skillMapper.toSkillDto(skill), offeresAmount));
        }
        return skillCandidateDtos;
    }

    @Override
    public void acquireSkillFromOffers(long skillId, long userId) {
        int minRequiredRecommendation = 3;
        if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) < minRequiredRecommendation) {
            log.error("Пользователь с id: {} не может присвоить скилл c id: {}, рекомендаций должно быть не менее 3.",
                    userId, skillId);
            throw new ForbiddenException("Для присвоения скилла должно быть не менее 3 рекомендаций.");
        }
        skillRepository.assignSkillToUser(skillId, userId);
        saveUserSkillGaranteeListToDb(skillId, userId);
        log.info("Скилл c id: {} успешно присвоен пользователю c id: {}", skillId, userId);
    }

    private void saveUserSkillGaranteeListToDb(long skillId, long userId) {
        User requester = userRepository.getByIdOrThrow(userId);
        Optional<Skill> optionalSkill = skillRepository.findById(skillId);
        if (optionalSkill.isEmpty()) {
            log.warn("Скилла с id: {}, который запросил пользователь с id: {} не существует.", skillId, userId);
            throw new EntityNotFoundException("Этот скилл присвоить нельзя, его не существует.");
        }
        Skill skill = optionalSkill.get();
        List<UserSkillGuarantee> userSkillGuarantees = new ArrayList<>();
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        for (SkillOffer skillOffer : skillOffers) {
            UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
            userSkillGuarantee.setUser(requester);
            userSkillGuarantee.setSkill(skill);
            userSkillGuarantee.setGuarantor(skillOffer.getRecommendation().getAuthor());
            userSkillGuarantees.add(userSkillGuarantee);
        }
        userSkillGuaranteeRepository.saveAll(userSkillGuarantees);
    }
}
