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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    void createSkill_whenSkillAlreadyExists_shouldThrowException() {
        SkillDto dto = new SkillDto();
        dto.setTitle("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(dto));

        verify(skillRepository).existsByTitle("Java");
        verifyNoMoreInteractions(skillRepository);
        verifyNoInteractions(skillMapper);
    }

    @Test
    void createSkill_whenSkillDoesNotExist_shouldSaveAndReturn() {
        SkillDto dto = new SkillDto();
        dto.setTitle("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(false);

        SkillDto result = skillService.create(dto);

        assertNotNull(result);
        assertEquals("Java", result.getTitle());

        verify(skillRepository).existsByTitle("Java");
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void testGetUserSkills() {
        Skill skill = new Skill();
        skill.setId(2L);
        skill.setTitle("Java");

        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill));

        List<SkillDto> userSkills = skillService.getUserSkills(userId);

        assertEquals(1, userSkills.size());
        assertEquals("Java", userSkills.get(0).getTitle());
    }

    @Test
    void acquireSkillFromOffers_withSkillNull_shouldReturnNull() {
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(null);

        Optional<Skill> result = skillRepository.findUserSkill(skillId, userId);
        assertNull(result);
    }

    @Test
    void acquireSkillFromOffers_skillNotFound_shouldThrowException() {
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));

        verify(skillRepository).findById(skillId);
    }

    @Test
    void acquireSkillFromOffers_userAlreadyHasSkill_shouldThrow() {
        Skill skill = new Skill();

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(List.of(new SkillOffer()));

        assertThrows(IllegalArgumentException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    void acquireSkillFromOffers_suggestedLessThanStandard_shouldThrow() {
        Skill skill = new Skill();

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(List.of(new SkillOffer()));

        assertThrows(IllegalArgumentException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    void acquireSkillFromOffers_shouldAssignAndSaveGuarantees() {
        Skill skill = new Skill();
        skill.setId(skillId);
        skill.setGuarantees(List.of(new UserSkillGuarantee()));

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

        verify(skillRepository).assignSkillToUser(skillId, userId);

        ArgumentCaptor<UserSkillGuarantee> captor = ArgumentCaptor.forClass(UserSkillGuarantee.class);
        verify(userSkillGuaranteeRepository, times(3)).save(captor.capture());

        List<UserSkillGuarantee> guarantees = captor.getAllValues();
        assertEquals(3, guarantees.size());
    }

    @Test
    void getOfferedSkills_shouldGroupAndMapCorrectly() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Java");

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill, skill, skill));

        List<SkillCandidateDto> candidates = skillService.getOfferedSkills(userId);
        SkillCandidateDto candidate = candidates.get(0);

        assertEquals("Java", candidate.getSkill().getTitle());
        assertEquals(3L, candidate.getOffersAmount());
    }
}

