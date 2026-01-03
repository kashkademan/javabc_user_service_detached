package school.faang.user_service.service.skill;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.skill.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private UserValidator userValidator;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Captor
    private ArgumentCaptor<Skill> captorSkill;
    @Captor
    private ArgumentCaptor<List<UserSkillGuarantee>> captorListUserSkillGuarantee;
    @Spy
    private SkillMapperImpl skillMapper;
    @Spy
    private SkillCandidateMapperImpl skillCandidateDto;

    @Test
    public void testSaveSkillRepository() {
        skillService.create(prepareDataSkillDto());
        verify(skillRepository, times(1)).save(captorSkill.capture());
        Skill skill = captorSkill.getValue();
        assertEquals(prepareDataSkillDto().getTitle(), skill.getTitle());
    }

    @Test
    public void returnUserSkills() {
        Long userId = 1L;
        when(skillRepository.findAllByUserId(userId)).thenReturn(getSkillList());

        List<SkillDto> result = skillService.getUserSkills(userId);
        assertEquals(result.size(), getSkillList().size());
        assertTrue(result.stream().anyMatch(skillDto -> getSkillList().get(0).getTitle().equals(skillDto.getTitle())));
    }

    @Test
    public void testReturnOfferedSkill() {
        Long userId = 1L;
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(prepareListSkill());
        when(skillRepository.findById(1L)).thenReturn(getOptionsSkill());
        when(skillRepository.findById(2L)).thenReturn(Optional.empty());

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getOffersAmount());
        assertEquals("Fly", result.get(0).getSkillDto().getTitle());
    }

    @Test
    public void testIfAcquireHas() {
        Long userId = 1L;
        Long skillId = 1L;
        preparationActions(skillId, userId, getOptionsSkill());

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);
        assertEquals(getOptionsSkill().get().getTitle(), result.getTitle());
    }


    @Test
    public void testSaveSkillGuarantees() {
        Long userId = 1L;
        Long skillId = 1L;
        preparationActions(skillId, userId, Optional.empty());

        skillService.acquireSkillFromOffers(skillId, userId);

        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(userSkillGuaranteeRepository, times(1)).saveAll(captorListUserSkillGuarantee.capture());
        List<UserSkillGuarantee> result = captorListUserSkillGuarantee.getValue();
        assertEquals(getSkillOffers(userId).get(0).getRecommendation().getAuthor().getId(), result.get(0).getGuarantor().getId());
        assertEquals(getSkillOffers(userId).get(1).getRecommendation().getReceiver().getId(), result.get(1).getUser().getId());
        assertEquals(getSkillOffers(userId).get(2).getSkill().getTitle(), result.get(2).getSkill().getTitle());
    }

    private void preparationActions(Long skillId, Long userId, Optional<Skill> optionalSkill) {
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(optionalSkill);
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(getSkillOffers(userId));
    }

    private @NotNull List<SkillOffer> getSkillOffers(Long userId) {
        List<SkillOffer> result = new ArrayList<>();
        int ListSize = 3;

        for (int i = 0; i < ListSize; i++) {
            result.add(
                    SkillOffer.builder()
                            .id(1L + i)
                            .skill(Skill.builder()
                                    .id(1L)
                                    .title("Fly")
                                    .build())
                            .recommendation(Recommendation.builder()
                                    .id(1L + i)
                                    .author(User.builder()
                                            .id(2L + i)
                                            .build())
                                    .receiver(User.builder()
                                            .id(userId)
                                            .build())
                                    .build())
                            .build());
        }
        return result;
    }

    private SkillDto prepareDataSkillDto() {
        return new SkillDto(1L, "Fly");
    }

    private @NotNull List<Skill> getSkillList() {
        return List.of(
                Skill.builder()
                        .id(1L)
                        .title("Fly")
                        .build(),
                Skill.builder()
                        .id(2L)
                        .title("test")
                        .build()
        );
    }

    private List<Skill> prepareListSkill() {
        return List.of(
                Skill.builder()
                        .id(1L)
                        .title("Fly")
                        .build(),
                Skill.builder()
                        .id(2L)
                        .title("test")
                        .build(),
                Skill.builder()
                        .id(1L)
                        .title("Fly")
                        .build());
    }

    private Optional<Skill> getOptionsSkill() {
        return Optional.of(Skill.builder()
                .id(1L)
                .title("Fly")
                .build());
    }
}