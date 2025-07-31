package school.faang.user_service.service.skill;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

/**
 * Сервисный класс, реализующий бизнес-логику для управления навыками пользователя.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Создание навыков (если они отсутствуют)</li>
 *   <li>Получение списка навыков пользователя</li>
 *   <li>Получение навыков, предложенных пользователю другими людьми</li>
 *   <li>Присвоение пользователю навыков из предложенных</li>
 * </ul>
 * <p>
 * Класс взаимодействует с хранилищами данных через репозитории
 * и использует {@code SkillMapper} для преобразования между сущностями и DTO.
 *
 * @author JasonRon
 * @since 19.07.2025
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SkillViewDto create(SkillCreateDto dto) {
        checkSkillExistByName(dto);
        Skill skill = skillMapper.toSkill(dto);
        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toSkillDto(savedSkill);
    }

    @Override
    public List<SkillViewDto> getByUserId(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        return skillRepository.findSkillsOfferedToUser(userId)
                .stream()
                .map(skill -> skillMapper.toSkillCandidateDto(skill, userId,
                        skillOfferRepository))
                .toList();
    }

    @Override
    @Transactional
    public void acquireSkillFromOffers(Long skillId, Long userId) {
        checkExistsById(skillId, userId);

        int offerCount = skillOfferRepository.countAllOffersOfSkill(skillId, userId);
        checkSkillExistenceBySentencesNumber(skillId, userId, offerCount);

        skillRepository.assignSkillToUser(skillId, userId);
        log.info("Навык с идентификатором {} "
                + "присвоенный пользователю с идентификатором {}", skillId, userId);
    }

    private void checkSkillExistenceBySentencesNumber(Long skillId, Long userId, int offerCount) {
        if (offerCount == 0) {
            throw new ForbiddenException("Навык не предлагается пользователю.");
        }

        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new ForbiddenException("Пользователь уже обладает этим навыком.");
        }
    }

    private void checkSkillExistByName(SkillCreateDto dto) {
        if (skillRepository.existsByTitle(dto.title())) {
            throw new ForbiddenException("Навык уже есть");
        }
    }

    private void checkExistsById(Long skillId, Long userId) {
        if (!(userRepository.existsById(userId))) {
            throw new EntityNotFoundException("Такого пользователя не существует");
        }
        if ((!skillRepository.existsById(skillId))) {
            throw new EntityNotFoundException("Такого навыка не существует");
        }
    }
}
