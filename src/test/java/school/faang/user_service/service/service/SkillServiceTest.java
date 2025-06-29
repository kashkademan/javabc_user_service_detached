package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillService skillService;

    private final long skillId = 1L;
    private final long userId = 1L;

    @Test
    public void createExistingSkill() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("title");

        when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreate() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("title");

        Skill entity = new Skill();
        entity.setTitle("title");

        when(skillRepository.existsByTitle("title")).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(entity);
        when(skillRepository.save(entity)).thenReturn(entity);
        when(skillMapper.toDto(entity)).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        assertNotNull(result);
        assertEquals("title", result.getTitle());

        verify(skillRepository, times(1)).save(entity);
    }

    @Test
    public void testGetUserSkills() {
        SkillDto dto = new SkillDto();
        dto.setId(2L);
        dto.setTitle("Java");
        Skill skill = new Skill();
        skill.setId(2L);
        skill.setTitle("Java");
        List<Skill> skills = List.of(skill);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        List<SkillDto> userSkills = skillService.getUserSkills(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);

        assertEquals(1, userSkills.size());
        assertEquals(dto.getId(), userSkills.get(0).getId());
        assertEquals(dto.getTitle(), userSkills.get(0).getTitle());
    }

    @Test
    public void acquireSkillFromOffers_WithSkillNull() {
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(null);
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);
        assertNull(userSkill);
    }

    @Test
    public void acquireSkillFromOffers_SkillNotFound() {
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
        verify(skillRepository, times(1)).findById(skillId);
    }

    @Test
    void testAcquireSkillFromOffers_UserAlreadyHasSkill() {
        Skill skill = new Skill();
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(List.of(new SkillOffer()));
        assertThrows(IllegalArgumentException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void acquireSkillFromOffers_SuggestedLessThanTheStandardValue() {
        Skill skill = new Skill();
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId))
                .thenReturn(List.of(new SkillOffer()));
        assertThrows(IllegalArgumentException.class,
                () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void acquireSkillFromOffers_verifyAccept() {

        Skill skill = new Skill();
        skill.setId(skillId);
        List<UserSkillGuarantee> userSkillGuarantees = List.of(new UserSkillGuarantee());
        skill.setGuarantees(userSkillGuarantees);

        SkillOffer offer1 = new SkillOffer();
        offer1.setSkill(skill);
        offer1.setRecommendation(new Recommendation());

        SkillOffer offer2 = new SkillOffer();
        offer2.setSkill(skill);
        offer2.setRecommendation(new Recommendation());

        SkillOffer offer3 = new SkillOffer();
        offer3.setSkill(skill);
        offer3.setRecommendation(new Recommendation());

        List<SkillOffer> offers = List.of(offer1, offer2, offer3);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);

        skillService.acquireSkillFromOffers(skillId, userId);

        verify(skillRepository, times(1)).assignSkillToUser(eq(skillId), eq(userId));

        ArgumentCaptor<UserSkillGuarantee> captor = ArgumentCaptor.forClass(UserSkillGuarantee.class);
        verify(userSkillGuaranteeRepository, times(3)).save(captor.capture());

        List<UserSkillGuarantee> capturedGuarantees = captor.getAllValues();
        assertNotNull(capturedGuarantees, "Captured guarantees list is null");
        assertEquals(3, capturedGuarantees.size(), "Unexpected number of guarantees saved");
    }

    @Test
    void createSkill_whenSkillAlreadyExists_shouldThrowException() {
        SkillDto dto = new SkillDto();
        dto.setTitle("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(dto));

        verify(skillRepository, times(1)).existsByTitle("Java");
        verifyNoMoreInteractions(skillRepository);
        verifyNoInteractions(skillMapper);
    }

    @Test
    void createSkill_whenSkillDoesNotExist_shouldSaveAndReturn() {
        SkillDto dto = new SkillDto();
        dto.setTitle("Java");
        Skill skill = new Skill();
        skill.setTitle("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        when(skillMapper.toEntity(dto)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(dto);

        SkillDto result = skillService.create(dto);

        verify(skillRepository, times(1)).existsByTitle("Java");
        verify(skillMapper, times(1)).toEntity(dto);
        verify(skillRepository, times(1)).save(skill);
        verify(skillMapper, times(1)).toDto(skill);

        assertEquals(dto.getTitle(), result.getTitle());
    }

    @Test
    void getOfferedSkills_shouldGroupAndMapCorrectly() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Java");
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("Java");
        when(skillRepository.findSkillsOfferedToUser(userId))
                .thenReturn(List.of(skill, skill, skill));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);
        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);
        assertEquals(1, result.size());
        SkillCandidateDto candidate = result.get(0);
        assertEquals("Java", candidate.getSkill().getTitle());
        assertEquals(3L, candidate.getOffersAmount());
    }
}


