package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${skills.min-offers-for-acquire}")
    private int minOffersForAcquire;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new DataValidationException(
                    "Навык с названием '" + skillDto.title() + "' уже существует."
            );
        }

        Skill skill = skillMapper.toSkill(skillDto);
        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toSkillDto(savedSkill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skills.stream().map(skill -> {
            List<UserDto> guarantors = userSkillGuaranteeRepository
                    .findAllByUserIdAndSkillId(userId, skill.getId()).stream()
                    .map(guarantee -> userMapper.toUserDto(guarantee.getGuarantor()))
                    .toList();

            return new SkillDto(
                    skill.getId(),
                    skill.getTitle(),
                    skill.getCreatedAt(),
                    skill.getUpdatedAt(),
                    guarantors
            );
        }).toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        return offeredSkills.stream()
                .map(skill -> {
                    int offersAmount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);

                    List<UserDto> guarantors = userSkillGuaranteeRepository
                            .findAllByUserIdAndSkillId(userId, skill.getId()).stream()
                            .map(UserSkillGuarantee::getGuarantor)
                            .map(userMapper::toUserDto)
                            .toList();

                    SkillDto skillDto = new SkillDto(
                            skill.getId(),
                            skill.getTitle(),
                            skill.getCreatedAt(),
                            skill.getUpdatedAt(),
                            guarantors
                    );

                    return skillCandidateMapper.toDto(skillDto, offersAmount);
                })
                .toList();
    }

    @Override
    @Transactional
    public void acquireSkillFromOffers(Long skillId, Long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("Пользователь уже имеет этот навык");
        }

        int offersCount = skillOfferRepository.countAllOffersOfSkill(skillId, userId);

        if (offersCount < minOffersForAcquire) {
            throw new DataValidationException("Недостаточно рекомендаций для приобретения навыка, нужно ещё "
                    + (minOffersForAcquire - offersCount)
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Умение не найдено"));

        skillRepository.assignSkillToUser(skillId, userId);

        Set<User> uniqueGuarantors = skillOfferRepository.findAllOffersOfSkill(skillId, userId).stream()
                .map(offer -> offer.getRecommendation().getAuthor())
                .collect(Collectors.toSet());

        List<UserSkillGuarantee> guarantees = uniqueGuarantors.stream()
                .map(guarantor -> UserSkillGuarantee.builder()
                        .user(user)
                        .skill(skill)
                        .guarantor(guarantor)
                        .build())
                .toList();

        userSkillGuaranteeRepository.saveAll(guarantees);
    }
}
