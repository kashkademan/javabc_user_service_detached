package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * RecommendationServiceImplTest — описание класса.
 * <p>
 *
 * </p>*
 *
 * @author bozya
 * @since 18.07.2025
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {
    @Mock
    private RecommendationDto recommendationDto;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserContext userContext;

    @Spy
    private RecommendationMapper recommendationMapper;

    @InjectMocks
    private RecommendationServiceImpl recommendationServiceImpl;

    @Test
    void testCreateRecommendation() {
        User userAuthor = new User();
        userAuthor.setId(1L);
        userAuthor.setUsername("Author");

        User userReceiver = new User();
        userReceiver.setId(2L);
        userReceiver.setUsername("Receiver");

        Recommendation savedRecommendation = new Recommendation();
        savedRecommendation.setId(1L);
        savedRecommendation.setAuthor(userAuthor);
        savedRecommendation.setReceiver(userReceiver);
        savedRecommendation.setContent("Все отлично");

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(savedRecommendation);

        when(recommendationRepository.findById(savedRecommendation.getId()))
                .thenReturn(Optional.of(savedRecommendation));


        recommendationServiceImpl.create(RecommendationServiceTestData.createDto());

        verify(recommendationRepository).save(any(Recommendation.class));
    }
}