package school.faang.user_service.service.skill;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("Проверка сервиса для работы с навыками")
public class SkillServiceImpTest {

    @InjectMocks
    private SkillServiceImpl service;
    @Mock
    private SkillRepository repository;
    @Spy
    private SkillMapper mapper;
    @Mock
    private SkillOfferRepository offerRepository;
    @Mock
    private SkillServiceValidator validator;
    @Mock
    private UserContext context;

    private final int countOperations = 1;
    private long skillId = 1L;
    private long userId = 2L;
    private String title = "title";
    private int offersAmount = 3;

    @Test
    @DisplayName("Проверка создания навыка с валидными данными")
    void testCreateSkill() {
        SkillCreateDto dto = new SkillCreateDto(title);
        Skill skill = new Skill();
        skill.setTitle(dto.title());
        when(mapper.toEntity(dto)).thenReturn(skill);
        Skill viewSkill = new Skill();
        viewSkill.setTitle(dto.title());
        viewSkill.setId(skillId);
        SkillViewDto resultSkill = new SkillViewDto(skillId, title);
        Mockito.when(repository.save(skill)).thenReturn(viewSkill);
        when(mapper.toViewDto(viewSkill)).thenReturn(resultSkill);

        SkillViewDto resultDto = service.create(dto);

        assertEquals(dto.title(), resultDto.title());
        assertEquals(skillId, resultDto.id());

        verify(repository, times(countOperations)).save(skill);
        verify(mapper, times(countOperations)).toViewDto(viewSkill);
    }

    @Test
    @DisplayName("Проверка получения списка навыков у пользователя по его Id с валидными данными")
    void testGetByUserId() {
        Skill skill = new Skill();
        skill.setTitle(title);
        skill.setId(skillId);
        Mockito.when(repository.findAllByUserId(userId)).thenReturn(List.of(skill));
        SkillViewDto skillViewDto = new SkillViewDto(skillId, title);
        when(mapper.toViewDto(skill)).thenReturn(skillViewDto);

        List<SkillViewDto> skillViewDtoList = List.of(skillViewDto);

        List<SkillViewDto> resultGetByUserId = service.getByUserId(userId);

        assertThat(resultGetByUserId).usingRecursiveAssertion().isEqualTo(skillViewDtoList);
        verify(repository, times(countOperations)).findAllByUserId(userId);
        verify(mapper, times(countOperations)).toViewDto(skill);
    }

    @Test
    @DisplayName("Проверка получения списка рекомендованных навыков пользователю по его Id с валидными данными")
    void testGetOfferedSkills() {
        when(context.getUserId()).thenReturn(userId);
        Skill skillOffered = new Skill();
        skillOffered.setId(skillId);
        skillOffered.setTitle(title);
        Mockito.when(repository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skillOffered));
        Mockito.when(offerRepository.countAllOffersOfSkill(skillOffered.getId(), userId))
                .thenReturn(offersAmount);
        SkillViewDto skillViewDto = new SkillViewDto(skillId, title);
        when(mapper.toViewDto(skillOffered)).thenReturn(skillViewDto);
        SkillOfferDto skillCandidateDto = new SkillOfferDto(skillViewDto, offersAmount);
        List<SkillOfferDto> skillCandidateDtoList = List.of(skillCandidateDto);

        List<SkillOfferDto> resultSkillCandidateDtoList = service.getOfferedSkills();

        assertThat(resultSkillCandidateDtoList).usingRecursiveAssertion().isEqualTo(skillCandidateDtoList);
        verify(context, times(countOperations)).getUserId();
        verify(repository, times(countOperations)).findSkillsOfferedToUser(userId);
        verify(offerRepository, times(countOperations)).countAllOffersOfSkill(skillOffered.getId(), userId);
        verify(mapper, times(countOperations)).toViewDto(skillOffered);
    }

    @Test
    @DisplayName("Добавление в базу данных предложенного навыка пользователю по валидным данным "
            + "Id пользователя и Id навыка ")
    void testAcquireSkillFromOffers() {
        when(context.getUserId()).thenReturn(userId);

        service.acquireSkillFromOffers(skillId);

        verify(context, times(countOperations)).getUserId();
        verify(repository, times(countOperations)).assignSkillToUser(skillId, userId);
    }
}
