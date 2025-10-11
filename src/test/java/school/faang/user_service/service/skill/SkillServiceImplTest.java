package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import school.faang.user_service.mapper.SkillMapperImpl;
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
    private SkillMapperImpl skillMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Skill> captor;

    private final Skill skill1 = mock(Skill.class);
    private final Skill skill2 = mock(Skill.class);
    private final long skill1Id = 10L;
    private final long skill2Id = 15L;
    private final Long userId = 1L;

    @Test
    void testCreateWithNullSkillDto() {
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

        verify(skillRepository, times(1)).save(captor.capture());
        Skill skill = captor.getValue();
        assertEquals(dto.title(), skill.getTitle());

        assertNotNull(result);
        assertEquals(dto.title(), result.title());
    }

    private CreateSkillDto prepareCreateSkillDto(boolean existsByTitle) {
        CreateSkillDto dto = new CreateSkillDto("title");
        when(skillRepository.existsByTitle(dto.title())).thenReturn(existsByTitle);
        return dto;
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

        assertThrows(DataValidationException.class, () -> skillService.getByUserId(userId));
    }

    @Test
    public void getByUserId_returnsEmptyListWhenNoSkills() {
        when(userContext.getUserId()).thenReturn(userId);
        when(skillRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<SkillDto> result = skillService.getByUserId(userId);

        assertTrue(result.isEmpty());
        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getByUserId_returnsSkillDtoWhenSkillsExist() {
        when(userContext.getUserId()).thenReturn(userId);

        when(skill1.getTitle()).thenReturn("Java");
        when(skill2.getTitle()).thenReturn("Spring");

        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill1, skill2));

        List<SkillDto> result = skillService.getByUserId(userId);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).title());
        assertEquals("Spring", result.get(1).title());
        verify(skillRepository, times(1)).findAllByUserId(userId);
        verify(skillMapper, times(1)).toSkillDto(skill1);
        verify(skillMapper, times(1)).toSkillDto(skill2);
    }

    @Test
    public void getOfferedSkills_returnsEmptyListWhenNoSkillsOffered() {
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(Collections.emptyList());

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertTrue(result.isEmpty());
        assertEquals(Collections.emptyList(), result);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    public void getOfferedSkills_returnsSkillCandidateDtoWhenSkillsExist() {
        when(skill1.getId()).thenReturn(skill1Id);
        when(skill2.getId()).thenReturn(skill2Id);
        when(skill1.getTitle()).thenReturn("Java");
        when(skill2.getTitle()).thenReturn("Spring");

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill1, skill2));
        when(skillOfferRepository.countAllOffersOfSkill(skill1.getId(), userId)).thenReturn(5);
        when(skillOfferRepository.countAllOffersOfSkill(skill2.getId(), userId)).thenReturn(3);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        SkillCandidateDto resultDto1 = result.get(0);
        SkillCandidateDto resultDto2 = result.get(1);

        assertEquals("Java", resultDto1.skill().title());
        assertEquals(5, resultDto1.offersAmount());
        assertEquals("Spring", resultDto2.skill().title());
        assertEquals(3, resultDto2.offersAmount());

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(skill1Id, userId);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(skill2Id, userId);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenSkillDoesNotExist() {
        long skillId = 1L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));

        verify(skillRepository, times(1)).findById(skillId);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenUserDoesNotExist() {
        when(skillRepository.findById(skill1Id)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(skill1Id, userId));

        verify(skillRepository, times(1)).findById(skill1Id);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenUserIsNotOwner() {
        long currentUserId = 2L; // userId = 1L - owner
        when(skillRepository.findById(skill1Id)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(currentUserId);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skill1Id, userId));

        verify(skillRepository, times(1)).findById(skill1Id);
        verify(userRepository, times(1)).findById(userId);
        verify(userContext, times(1)).getUserId();
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenUserAlreadyHasSkill() {
        when(skillRepository.findById(skill1Id)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(userId);
        when(skillRepository.findUserSkill(skill1Id, userId)).thenReturn(Optional.of(mock(Skill.class)));

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skill1Id, userId));

        verify(skillRepository, times(1)).findById(skill1Id);
        verify(userRepository, times(1)).findById(userId);
        verify(userContext, times(1)).getUserId();
        verify(skillRepository, times(1)).findUserSkill(skill1Id, userId);
        verifyNoMoreInteractions(skillRepository);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(skillOfferRepository);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenSkillWasNotOffered() {
        when(skillRepository.findById(skill1Id)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(userId);
        when(skillRepository.findUserSkill(skill1Id, userId)).thenReturn(Optional.empty());
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skill1Id, userId));

        verify(skillRepository, times(1)).findById(skill1Id);
        verify(userRepository, times(1)).findById(userId);
        verify(userContext, times(1)).getUserId();
        verify(skillRepository, times(1)).findUserSkill(skill1Id, userId);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verifyNoMoreInteractions(skillRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @BeforeEach
    void setUpOne() {
        ReflectionTestUtils.setField(skillService, "minSkillRecommendationsCount", 3);
        when(skill1.getId()).thenReturn(skill1Id);
        when(skill2.getId()).thenReturn(skill2Id);
    }

    @Test
    public void acquireSkillFromOffers_throwsWhenRecommendationsAreLessThanMin() {
        when(skillRepository.findById(skill1Id)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(userId);
        when(skillRepository.findUserSkill(skill1Id, userId)).thenReturn(Optional.empty());
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill1, skill2));
        when(skillOfferRepository.countAllOffersOfSkill(skill1Id, userId)).thenReturn(2);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skill1Id, userId));

        verify(skillRepository, times(1)).findById(skill1Id);
        verify(userRepository, times(1)).findById(userId);
        verify(userContext, times(1)).getUserId();
        verify(skillRepository, times(1)).findUserSkill(skill1Id, userId);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(skill1Id, userId);
        verify(skillRepository, never()).assignSkillToUser(skill1Id, userId);
    }

    @BeforeEach
    void setUpTwo() {
        ReflectionTestUtils.setField(skillService, "minSkillRecommendationsCount", 3);
        when(skill1.getId()).thenReturn(skill1Id);
        when(skill2.getId()).thenReturn(skill2Id);
    }

    @Test
    public void acquireSkillFromOffers_assignsSkillToUserSuccessfully() {
        when(skillRepository.findById(skill1Id)).thenReturn(Optional.of(skill1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(userContext.getUserId()).thenReturn(userId);
        when(skillRepository.findUserSkill(skill1Id, userId)).thenReturn(Optional.empty());
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill1, skill2));
        when(skillOfferRepository.countAllOffersOfSkill(skill1Id, userId)).thenReturn(3);

        skillService.acquireSkillFromOffers(skill1Id, userId);

        verify(skillRepository, times(1)).findById(skill1Id);
        verify(userRepository, times(1)).findById(userId);
        verify(userContext, times(1)).getUserId();
        verify(skillRepository, times(1)).findUserSkill(skill1Id, userId);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(skill1Id, userId);
        verify(skillRepository, times(1)).assignSkillToUser(skill1Id, userId);
    }
}
