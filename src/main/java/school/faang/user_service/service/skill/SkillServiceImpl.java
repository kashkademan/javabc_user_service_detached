package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl implements SkillService {
    SkillRepository skillRepository;
    SkillMapper skillMapper;
    SkillOfferRepository skillOfferRepository;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        log.info("Получили новый объект по RestAPI: {} ", skillDto);
        Skill skill = skillMapper.toSkill(skillDto);
        log.info("Проверяем, нет ли уже такого заголовка в базе");
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Данный заголовок уже существует");
        }

        skill = skillRepository.save(skill);
        log.info("В базу сохранен новый объект: {} ", skill);
        return skillMapper.toSkillDto(skill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        if (skills.isEmpty()) {
            throw new EntityNotFoundException("По заданному id не найдено ни одной записи");
        }
        return skills.stream()
                .map(skill -> skillMapper.toSkillDto(skill))
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skills.stream()
                .map(skill -> skillMapper.toSkillDto(skill))
                .map(skill -> {
                    int offersAmount = skillOfferRepository.countAllOffersOfSkill(skill.id(), userId);
                    return new SkillCandidateDto(skill, offersAmount);
                })
                .toList();
    }

    @Override
    @Transactional
    public void acquireSkillFromOffers(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }
}
