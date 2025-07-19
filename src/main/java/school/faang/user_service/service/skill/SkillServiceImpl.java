package school.faang.user_service.service.skill;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

/*

${SkillServiceImpl} — сервисный класс, реализующий бизнес-логику, связанную с управлением навыками пользователя.
<p>
TODO:
 Методы:
 Создание навыков(Если их еще нет).
 Получение списка навыков пользователя.
 Получение навыков, предложенных пользователелю другими.
 Присвоение навыков пользователю из предложенных.
 Что делает:
 Класс взаимодействует с хранилищами данных через репозитории,
 а также использует SkillMapper для преобразованияй(Сущности и dto).
</p>*
@author ${JasonRon}
@since ${19.07.2025}*/

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;

    @Override
    public SkillDto create(CreateSkillDto dto) {
        if (skillRepository.existsByTitle(dto.title())) {
            throw new IllegalStateException("Скилл уже есть");
        }
        Skill skill = skillMapper.toSkill(dto);
        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toSkillDto(savedSkill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId)
                .stream()
                .map(skill -> {
                    int offers = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
                    return new SkillCandidateDto(skillMapper.toSkillDto(skill), offers);
                })
                .toList();
    }

    @Override
    @Transactional
    public void acquireSkillFromOffers(long skillId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()
                        -> new IllegalArgumentException("Пользователь не найден: " + userId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Скилл не найден: " + skillId));

        int offerCount = skillOfferRepository.countAllOffersOfSkill(skillId, userId);
        if (offerCount == 0) {
            throw new IllegalStateException("Навык не предлагается пользователю.");
        }

        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new IllegalStateException("Пользователь уже обладает этим навыком.");
        }

        skillRepository.assignSkillToUser(skillId, userId);
        log.info("Навык с идентификатором {} " +
                "присвоенный пользователю с идентификатором {}", skillId, userId);
    }
}
