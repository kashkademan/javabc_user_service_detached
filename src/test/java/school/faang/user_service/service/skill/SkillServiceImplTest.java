package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты SkillServiceImpl")
public class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;

    @BeforeEach
    public void setUp() {
        skillService.setMinimalSkillOffers(3);
        // Здесь можно инициализировать необходимые объекты или моки перед каждым тестом
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @DisplayName("SkillServiceImpl create - Успешное создание скилла")
    @ValueSource(strings = {"Java", "Python", "JavaScript", "C++", "Go"})
    public void createSkill_ReturnsCreatedSkill(String inputData) {
        CreateSkillDto createSkillDto = CreateSkillDto.builder()
                .title(inputData)
                .build();
        SkillDto skillDto = SkillDto.builder()
                .id(1L)
                .title(inputData)
                .build();
        Skill skill = Skill.builder()
                .id(1L)
                .title(inputData)
                .build();

        when(skillMapper.toSkill(createSkillDto)).thenReturn(skill);
        when(skillMapper.toSkillDto(skill)).thenReturn(skillDto);
        when(skillRepository.existsByTitle(inputData)).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skill);

        verify(skillMapper).toSkill(createSkillDto);

        SkillDto skillDtoResult = skillService.create(createSkillDto);
        assertThat(skillDtoResult.title()).isEqualTo(inputData);
        assertThat(skillDtoResult.id()).isEqualTo(1L);



    }

    @Test
    @DisplayName("SkillServiceImpl create - Ошибка при создании скилла с существующим названием")
    public void createSkill_ThrowsDataValidationException() {
        CreateSkillDto createSkillDto = CreateSkillDto.builder()
                .title("Java")
                .build();
        Skill skill = Skill.builder()
                .id(1L)
                .title("Java")
                .build();

        when(skillMapper.toSkill(createSkillDto)).thenReturn(skill);
        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        verify(skillRepository).existsByTitle("Java");

        assertThatThrownBy(() -> skillService.create(createSkillDto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Данный заголовок уже существует: Java");
    }

    @Test
    @DisplayName("SkillServiceImpl getByUserId - Получение скиллов пользователя")
    public void getByUserId_ReturnsSkills() {
        long userId = 1L;
        Skill skill = Skill.builder()
                .id(1L)
                .title("Java")
                .build();
        SkillDto skillDto = SkillDto.builder()
                .id(1L)
                .title("Java")
                .build();

        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill));
        when(skillMapper.toSkillDto(skill)).thenReturn(skillDto);

        verify(skillRepository).findAllByUserId(userId);

        List<SkillDto> skills = skillService.getByUserId(userId);
        assertThat(skills.get(0).title()).isEqualTo("Java");
        assertThat(skills.get(0).id()).isEqualTo(1L);

    }

    @Test
    @DisplayName("SkillServiceImpl getOfferedSkills - Получение предложенных скиллов")
    public void getOfferedSkills_ReturnsOfferedSkills() {
        long userId = 1L;
        Skill skill = Skill.builder()
                .id(1L)
                .title("Java")
                .build();
        SkillDto skillDto = SkillDto.builder()
                .id(1L)
                .title("Java")
                .build();

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill));
        when(skillMapper.toSkillDto(skill)).thenReturn(skillDto);
        when(skillOfferRepository.countAllOffersOfSkill(1L, userId)).thenReturn(3);

        List<SkillCandidateDto> offeredSkills = skillService.getOfferedSkills(userId);
        assertThat(offeredSkills.get(0).skill().title()).isEqualTo("Java");
        assertThat(offeredSkills.get(0).offersAmount()).isEqualTo(3);
    }

    @Test
    @DisplayName("SkillServiceImpl acquireSkillFromOffers - Успешное приобретение скилла")
    public void acquireSkillFromOffers_ReturnsOk() {
        long skillId = 1L;
        long userId = 1L;

        when(skillRepository.existsById(skillId)).thenReturn(false);
        when(skillOfferRepository.countAllOffersOfSkill(skillId, userId)).thenReturn(3);
        doNothing().when(skillRepository).assignSkillToUser(skillId, userId);

        verify(skillRepository).assignSkillToUser(skillId, userId);

        skillService.acquireSkillFromOffers(skillId, userId);
    }

    @Test
    @DisplayName("SkillServiceImpl acquireSkillFromOffers - Ошибка при приобретении скилла без достаточных предложений")
    public void acquireSkillFromOffers_ThrowsIllegalStateException() {
        long skillId = 1L;
        long userId = 1L;


        when(skillRepository.existsById(skillId)).thenReturn(false);
        when(skillOfferRepository.countAllOffersOfSkill(skillId, userId)).thenReturn(2);

        verify(skillRepository, never()).assignSkillToUser(skillId, userId);

        assertThatThrownBy(() -> skillService.acquireSkillFromOffers(skillId, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Требуется не менее трех предложений скилла для его приобретения");

    }
}