package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {
    @InjectMocks
    private SkillServiceImpl skillServiceImpl;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Skill> skillCaptor;

    @Test
    public void createExistentSkill() {
        CreateSkillDto createSkillDto = new CreateSkillDto("title");

        when(skillRepository.existsByTitle(createSkillDto.title())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillServiceImpl.create(createSkillDto));
    }

    @Test
    public void createCreates() {
        CreateSkillDto createSkillDto = new CreateSkillDto("title");

        when(skillRepository.existsByTitle(createSkillDto.title())).thenReturn(false);

        skillServiceImpl.create(createSkillDto);

        verify(skillRepository, times(1)).save(any(Skill.class));
    }

    @Test
    public void getByUserIdReturnsSkill() {
        Long userId = 1L;
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        SkillOffer skillOffer = new SkillOffer();
        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(new User());
        skillOffer.setRecommendation(recommendation);

        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill));
        when(skillOfferRepository.findAllOffersOfSkill(skill.getId(), userId)).thenReturn(List.of(skillOffer));

        skillServiceImpl.getByUserId(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skill.getId(), userId);
    }


    @Test
    public void getOfferedSkillsWithEmptyOffersList() {
        long userId = 1L;

        assertThrows(EntityNotFoundException.class, () -> skillServiceImpl.getOfferedSkills(userId));
    }

    @Test
    public void getOfferedSkillsReturnsOffers() {
        long userId = 1L;
        Skill skill = new Skill();
        skill.setId(1L);

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill));
        when(skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId)).thenReturn(1);

        skillServiceImpl.getOfferedSkills(userId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(skill.getId(), userId);
    }

    @Test
    public void acquireSkillFromOffersWhenNotEnoughRecommendations() {
        long skillId = 1L;
        long userId = 1L;

        when(skillOfferRepository.countAllOffersOfSkill(skillId, userId)).thenReturn(2);

        assertThrows(ForbiddenException.class, () -> skillServiceImpl.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void acquireSkillFromOffers() {
        long skillId = 1L;
        long userId = 1L;
        SkillOffer skillOffer = new SkillOffer();
        Recommendation recommendation = new Recommendation();
        User author = new User();
        recommendation.setAuthor(author);
        skillOffer.setRecommendation(recommendation);

        when(skillOfferRepository.countAllOffersOfSkill(skillId, userId)).thenReturn(5);
        when(userRepository.getByIdOrThrow(userId)).thenReturn(new User());
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(new Skill()));
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(List.of(skillOffer));

        skillServiceImpl.acquireSkillFromOffers(skillId, userId);

        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(skillId, userId);
        verify(userRepository, times(1)).getByIdOrThrow(userId);
        verify(skillRepository, times(1)).findById(skillId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
    }
}
