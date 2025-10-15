package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {

    @InjectMocks
    private SkillServiceImpl skillService;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private final SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Skill> captor;

    private static final long SKILL1_ID = 10L;
    private static final long SKILL2_ID = 15L;
    private static final long USER_ID = 1L;
    private final Skill skill1 = Skill.builder().id(SKILL1_ID).title("Java").build();
    private final Skill skill2 = Skill.builder().id(SKILL2_ID).title("Spring").build();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(skillService, "minSkillRecommendationsCount", 3);
    }

    @Test
    public void testCreateWithNullSkillDto() {
        assertThrows(DataValidationException.class, () -> skillService.create(null));
    }

    @Test
    public void testCreateWithNullTitle() {
        CreateSkillDto dto = new CreateSkillDto(null);

        assertThrows(DataValidationException.class, () -> skillService.create(dto));
    }

    @Test
    public void testCreateWithBlankTitle() {
        CreateSkillDto dto = new CreateSkillDto("  ");

        assertThrows(DataValidationException.class, () -> skillService.create(dto));
    }

    @Test
    public void testCreateWithExistingTitle() {
        CreateSkillDto dto = prepareCreateSkillDto(true);

        assertThrows(DataValidationException.class, () -> skillService.create(dto));
    }

    @Test
    public void testCreateSavesSkill() {
        CreateSkillDto dto = prepareCreateSkillDto(false);

        Skill skillToSave = skillMapper.toSkill(dto); // Создаем объект такой же, как в сервисе
        when(skillRepository.save(any(Skill.class))).thenReturn(skillToSave);

        SkillDto result = skillService.create(dto);

        verify(skillRepository).save(captor.capture());
        Skill skill = captor.getValue();
        assertEquals(dto.title(), skill.getTitle());

        assertNotNull(result);
        assertEquals(dto.title(), result.title());
    }

    @Test
    public void getByUserId_throwsWhenUserIdIsNull() {
        assertThrows(DataValidationException.class, () -> skillService.getByUserId(null));
    }

    @Test
    public void getByUserId_throwsWhenUserIdIsNegative() {
        assertThrows(DataValidationException.class, () -> skillService.getByUserId(-1L));
    }

    @Test
    public void getByUserId_throwsWhenUserIsNotOwner() {
        Long currentUserId = 2L;
        when(userContext.getUserId()).thenReturn(currentUserId);

        assertThrows(DataValidationException.class, () -> skillService.getByUserId(USER_ID));
    }

    @Test
    public void getByUserId_returnsEmptyListWhenNoSkills() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());

        List<SkillDto> result = skillService.getByUserId(USER_ID);

        assertTrue(result.isEmpty());
        verify(skillRepository, times(1)).findAllByUserId(USER_ID);
    }

    @Test
    public void getByUserId_returnsSkillDtoWhenSkillsExist() {
        when(userContext.getUserId()).thenReturn(USER_ID);

        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(List.of(skill1, skill2));

        List<SkillDto> result = skillService.getByUserId(USER_ID);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).title());
        assertEquals("Spring", result.get(1).title());
        verify(skillRepository, times(1)).findAllByUserId(USER_ID);
        verify(skillMapper, times(1)).toSkillDto(skill1);
        verify(skillMapper, times(1)).toSkillDto(skill2);
    }

    @Test
    public void getOfferedSkills_returnsEmptyListWhenNoSkillsOffered() {
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(Collections.emptyList());

        List<SkillCandidateDto> result = skillService.getOfferedSkills(USER_ID);

        assertTrue(result.isEmpty());
        verify(skillRepository, times(1)).findSkillsOfferedToUser(USER_ID);
    }

    @Test
    public void getOfferedSkills_returnsSkillCandidateDtoWhenSkillsExist() {
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of(skill1, skill2));
        when(skillOfferRepository.countAllOffersOfSkill(skill1.getId(), USER_ID)).thenReturn(5);
        when(skillOfferRepository.countAllOffersOfSkill(skill2.getId(), USER_ID)).thenReturn(3);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(USER_ID);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        SkillCandidateDto resultDto1 = result.get(0);
        SkillCandidateDto resultDto2 = result.get(1);

        assertEquals("Java", resultDto1.skill().title());
        assertEquals(5, resultDto1.offersAmount());
        assertEquals("Spring", resultDto2.skill().title());
        assertEquals(3, resultDto2.offersAmount());

        verify(skillRepository, times(1)).findSkillsOfferedToUser(USER_ID);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(SKILL1_ID, USER_ID);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(SKILL2_ID, USER_ID);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenSkillDoesNotExist() {
        long skillId = 1L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(skillId, USER_ID));

        verify(skillRepository, times(1)).findById(skillId);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenUserDoesNotExist() {
        when(skillRepository.findById(SKILL1_ID)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(SKILL1_ID, USER_ID));

        verify(skillRepository, times(1)).findById(SKILL1_ID);
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenUserIsNotOwner() {
        long currentUserId = 2L; // userId = 1L - owner
        when(skillRepository.findById(SKILL1_ID)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(currentUserId);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL1_ID, USER_ID));

        verify(skillRepository, times(1)).findById(SKILL1_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userContext, times(1)).getUserId();
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenUserAlreadyHasSkill() {
        when(skillRepository.findById(SKILL1_ID)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findUserSkill(SKILL1_ID, USER_ID)).thenReturn(Optional.of(mock(Skill.class)));

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL1_ID, USER_ID));

        verify(skillRepository, times(1)).findById(SKILL1_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userContext, times(1)).getUserId();
        verify(skillRepository, times(1)).findUserSkill(SKILL1_ID, USER_ID);
        verifyNoMoreInteractions(skillRepository);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(skillOfferRepository);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenSkillWasNotOffered() {
        when(skillRepository.findById(SKILL1_ID)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findUserSkill(SKILL1_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL1_ID, USER_ID));

        verify(skillRepository, times(1)).findById(SKILL1_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userContext, times(1)).getUserId();
        verify(skillRepository, times(1)).findUserSkill(SKILL1_ID, USER_ID);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(USER_ID);
        verifyNoMoreInteractions(skillRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenRecommendationsAreLessThanMin() {
        when(skillRepository.findById(SKILL1_ID)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findUserSkill(SKILL1_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of(skill1, skill2));
        when(skillOfferRepository.countAllOffersOfSkill(SKILL1_ID, USER_ID)).thenReturn(2);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL1_ID, USER_ID));

        verify(skillRepository, times(1)).findById(SKILL1_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userContext, times(1)).getUserId();
        verify(skillRepository, times(1)).findUserSkill(SKILL1_ID, USER_ID);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(USER_ID);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(SKILL1_ID, USER_ID);
        verify(skillRepository, never()).assignSkillToUser(SKILL1_ID, USER_ID);
    }

    @Test
    public void acquireSkillFromOffers_assignsSkillToUserSuccessfully() {
        when(skillRepository.findById(SKILL1_ID)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findUserSkill(SKILL1_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of(skill1, skill2));
        when(skillOfferRepository.countAllOffersOfSkill(SKILL1_ID, USER_ID)).thenReturn(3);

        skillService.acquireSkillFromOffers(SKILL1_ID, USER_ID);

        verify(skillRepository, times(1)).findById(SKILL1_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userContext, times(1)).getUserId();
        verify(skillRepository, times(1)).findUserSkill(SKILL1_ID, USER_ID);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(USER_ID);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(SKILL1_ID, USER_ID);
        verify(skillRepository, times(1)).assignSkillToUser(SKILL1_ID, USER_ID);
    }

    private CreateSkillDto prepareCreateSkillDto(boolean existsByTitle) {
        CreateSkillDto dto = new CreateSkillDto("title");
        when(skillRepository.existsByTitle(dto.title())).thenReturn(existsByTitle);
        return dto;
    }
}
