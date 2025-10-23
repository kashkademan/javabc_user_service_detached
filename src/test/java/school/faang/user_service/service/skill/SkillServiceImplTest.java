package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
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

    private String anyTitle;
    private long anySkillId;
    private long anyLong;
    private long anyUserId;
    private int anyIntLessThanThree;
    private int anyIntMoreThanThree;
    private Skill anySkill;
    private User anyUser;

    @BeforeEach
    void setUp() {
        anyTitle = "anyTitle";
        anyLong = 1L;

        anySkillId = anyLong;
        anyUserId = anyLong;
        anySkill = new Skill();
        anySkill.setId(anyLong);
        anySkill.setTitle(anyTitle);
        anyUser = new User();
        anyIntLessThanThree = 2;
        anyIntMoreThanThree = 4;
    }

    @Test
    public void createExistentSkill() {
        CreateSkillDto createSkillDto = new CreateSkillDto(anyTitle);

        when(skillRepository.existsByTitle(createSkillDto.title())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillServiceImpl.create(createSkillDto));
    }

    @Test
    public void createCreates() {
        CreateSkillDto createSkillDto = new CreateSkillDto(anyTitle);

        when(skillRepository.existsByTitle(createSkillDto.title())).thenReturn(false);

        skillServiceImpl.create(createSkillDto);

        verify(skillRepository, times(1)).save(any(Skill.class));
    }

    @Test
    public void getByUserIdReturnsSkill() {
        SkillOffer skillOffer = new SkillOffer();
        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(anyUser);
        skillOffer.setRecommendation(recommendation);

        when(skillRepository.findAllByUserId(anyUserId)).thenReturn(List.of(anySkill));
        when(skillOfferRepository.findAllOffersOfSkill(anySkill.getId(), anyUserId)).thenReturn(List.of(skillOffer));

        skillServiceImpl.getByUserId(anyUserId);

        verify(skillRepository, times(1)).findAllByUserId(anyUserId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(anySkill.getId(), anyUserId);
    }


    @Test
    public void getOfferedSkillsWithEmptyOffersList() {
        assertThrows(EntityNotFoundException.class, () -> skillServiceImpl.getOfferedSkills(anyUserId));
    }

    @Test
    public void getOfferedSkillsReturnsOffers() {
        when(skillRepository.findSkillsOfferedToUser(anyUserId)).thenReturn(List.of(anySkill));
        when(skillOfferRepository.countAllOffersOfSkill(anySkillId, anyUserId)).thenReturn(anyIntLessThanThree);

        skillServiceImpl.getOfferedSkills(anyUserId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(anyUserId);
        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(anySkill.getId(), anyUserId);
    }

    @Test
    public void acquireSkillFromOffersWhenNotEnoughRecommendations() {
        when(skillOfferRepository.countAllOffersOfSkill(anySkillId, anyUserId)).thenReturn(anyIntLessThanThree);

        assertThrows(ForbiddenException.class, () -> skillServiceImpl.acquireSkillFromOffers(anySkillId, anyUserId));
    }

    @Test
    public void acquireSkillFromOffers() {
        SkillOffer skillOffer = new SkillOffer();
        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(anyUser);
        skillOffer.setRecommendation(recommendation);

        when(skillOfferRepository.countAllOffersOfSkill(anySkillId, anyUserId)).thenReturn(anyIntMoreThanThree);
        when(userRepository.getByIdOrThrow(anyUserId)).thenReturn(anyUser);
        when(skillRepository.findById(anySkillId)).thenReturn(Optional.of(anySkill));
        when(skillOfferRepository.findAllOffersOfSkill(anySkillId, anyUserId)).thenReturn(List.of(skillOffer));

        skillServiceImpl.acquireSkillFromOffers(anySkillId, anyUserId);

        verify(skillOfferRepository, times(1)).countAllOffersOfSkill(anySkillId, anyUserId);
        verify(userRepository, times(1)).getByIdOrThrow(anyUserId);
        verify(skillRepository, times(1)).findById(anySkillId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(anySkillId, anyUserId);
    }
}
