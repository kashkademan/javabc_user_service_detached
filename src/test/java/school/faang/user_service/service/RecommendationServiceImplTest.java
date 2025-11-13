package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.recommendation.RecommendationServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @Mock
    private UserContext userContext;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_RECEIVER_ID = 5L;
    private static final Long TEST_RECOMMENDATION_ID = 10L;
    private static final String TEST_CONTENT_TEXT = "Test text";
    private static final LocalDateTime TEST_CREATED_AT = LocalDateTime.now().minusMonths(7);
    private static final LocalDateTime INVALID_CREATED_AT = LocalDateTime.now();
    private final User testAuthor = new User();
    private final User testReceiver = new User();

    Recommendation recommendation1 = new Recommendation();
    Recommendation recommendation2 = new Recommendation();
    Recommendation recommendation3 = new Recommendation();
    CreateRecommendationDto createRecommendationDto = new CreateRecommendationDto(TEST_RECEIVER_ID, TEST_CONTENT_TEXT);
    UpdateRecommendationDto updateRecommendationDto = new UpdateRecommendationDto(TEST_CONTENT_TEXT);

    @BeforeEach
    void setUp() {
        testAuthor.setId(TEST_USER_ID);
        testReceiver.setId(TEST_RECEIVER_ID);

        recommendation1.setId(TEST_RECOMMENDATION_ID);
        recommendation1.setAuthor(testAuthor);
        recommendation1.setReceiver(testReceiver);
        recommendation1.setCreatedAt(TEST_CREATED_AT);
        recommendation1.setContent(TEST_CONTENT_TEXT);

        recommendation2.setAuthor(testAuthor);
        recommendation2.setReceiver(testReceiver);
        recommendation2.setCreatedAt(TEST_CREATED_AT.minusMonths(1));
        recommendation2.setContent(TEST_CONTENT_TEXT);

        recommendation3.setAuthor(testAuthor);
        recommendation3.setReceiver(testReceiver);
        recommendation3.setCreatedAt(TEST_CREATED_AT.minusMonths(2));
        recommendation3.setContent(TEST_CONTENT_TEXT);
    }

    @Test
    void testCreateSuccess() {
        List<Recommendation> testRecommendationList = List.of(recommendation1, recommendation2, recommendation3);

        Mockito.when(userContext.getUserId()).thenReturn(TEST_USER_ID);
        Mockito.when(recommendationRepository.findAllByAuthorId(TEST_USER_ID)).thenReturn(testRecommendationList);
        Mockito.when(recommendationRepository.create(TEST_USER_ID, TEST_RECEIVER_ID, TEST_CONTENT_TEXT))
                .thenReturn(TEST_RECOMMENDATION_ID);
        Mockito.when(recommendationRepository.findById(TEST_RECOMMENDATION_ID))
                .thenReturn(Optional.of(recommendation1));

        RecommendationDto expectedDto = new RecommendationDto(TEST_RECOMMENDATION_ID,
                TEST_USER_ID, TEST_RECEIVER_ID, TEST_CONTENT_TEXT);
        RecommendationDto resultDto = recommendationService.create(createRecommendationDto);
        assertEquals(expectedDto, resultDto);
    }

    @Test
    void testCreateInvalidReceiverId() {
        CreateRecommendationDto invalidReceivedIdDto = new CreateRecommendationDto(TEST_USER_ID, TEST_CONTENT_TEXT);
        assertThrows(DataValidationException.class, () -> recommendationService.create(invalidReceivedIdDto),
                "Leaving a recommendation to self is not allowed!");
    }

    @Test
    void testCreateRecentRecommendationAlreadyExists() {
        Recommendation recentRecommendation = new Recommendation();
        recentRecommendation.setAuthor(testAuthor);
        recentRecommendation.setReceiver(testReceiver);
        recentRecommendation.setCreatedAt(INVALID_CREATED_AT);

        List<Recommendation> testRecommendationList = List
                .of(recommendation1, recommendation2, recommendation3, recentRecommendation);

        Mockito.when(userContext.getUserId()).thenReturn(TEST_USER_ID);
        Mockito.when(recommendationRepository.findAllByAuthorId(TEST_USER_ID)).thenReturn(testRecommendationList);

        assertThrows(ForbiddenException.class, () -> recommendationService.create(createRecommendationDto),
                "Last recommendation for this user was created later than 6 months ago");
    }

    @Test
    void testRecommendationHasNotBeenCreated() {
        List<Recommendation> testRecommendationList = List.of(recommendation1, recommendation2, recommendation3);

        Mockito.when(userContext.getUserId()).thenReturn(TEST_USER_ID);
        Mockito.when(recommendationRepository.findAllByAuthorId(TEST_USER_ID)).thenReturn(testRecommendationList);
        Mockito.when(recommendationRepository.create(TEST_USER_ID, TEST_RECEIVER_ID, TEST_CONTENT_TEXT))
                .thenReturn(TEST_RECOMMENDATION_ID);
        Mockito.when(recommendationRepository.findById(TEST_RECOMMENDATION_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationService.create(createRecommendationDto),
                "Recommendation has not been created");
    }

    @Test
    void testUpdateSuccess() {
        Mockito.when(userContext.getUserId())
                .thenReturn(TEST_USER_ID);
        Mockito.when(recommendationRepository.findById(TEST_RECOMMENDATION_ID))
                .thenReturn(Optional.of(recommendation1));
        Mockito.when(recommendationRepository.save(Mockito.any(Recommendation.class))).thenReturn(recommendation1);

        RecommendationDto expectedDto = new RecommendationDto(TEST_RECOMMENDATION_ID,
                TEST_USER_ID, TEST_RECEIVER_ID, TEST_CONTENT_TEXT);

        RecommendationDto resultDto = recommendationService.update(TEST_RECOMMENDATION_ID, updateRecommendationDto);
        assertEquals(expectedDto, resultDto);
    }

    @Test
    void testUpdateRecommendationDoesNotExist() {
        Mockito.when(userContext.getUserId()).thenReturn(TEST_USER_ID);
        Mockito.when(recommendationRepository.findById(TEST_RECOMMENDATION_ID)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> recommendationService
                        .update(TEST_RECOMMENDATION_ID, updateRecommendationDto),
                "Recommendation with this ID does not exist!");
    }

    @Test
    void testUpdateInvalidAuthorId() {
        Mockito.when(userContext.getUserId()).thenReturn(TEST_RECEIVER_ID);
        Mockito.when(recommendationRepository.findById(TEST_RECOMMENDATION_ID))
                .thenReturn(Optional.of(recommendation1));
        assertThrows(ForbiddenException.class, () -> recommendationService
                        .update(TEST_RECOMMENDATION_ID, updateRecommendationDto),
                "Author ID mismatch, editing this recommendation is not allowed for this user!");
    }

    @Test
    void testDeleteSuccess() {
        Mockito.when(userContext.getUserId()).thenReturn(TEST_USER_ID);
        Mockito.when(recommendationRepository.findById(TEST_RECOMMENDATION_ID))
                .thenReturn(Optional.of(recommendation1));

        recommendationService.delete(TEST_RECOMMENDATION_ID);
        Mockito.verify(recommendationRepository).deleteByIdAndAuthor_id(TEST_RECOMMENDATION_ID, TEST_USER_ID);
    }

    @Test
    void testDeleteRecommendationDoesNotExist() {
        Mockito.when(userContext.getUserId()).thenReturn(TEST_USER_ID);
        Mockito.when(recommendationRepository.findById(TEST_RECOMMENDATION_ID)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> recommendationService
                        .delete(TEST_RECOMMENDATION_ID),
                "Recommendation with this ID does not exist!");
    }

    @Test
    void testDeleteInvalidAuthorId() {
        Mockito.when(userContext.getUserId()).thenReturn(TEST_RECEIVER_ID);
        Mockito.when(recommendationRepository.findById(TEST_RECOMMENDATION_ID))
                .thenReturn(Optional.of(recommendation1));
        assertThrows(ForbiddenException.class, () -> recommendationService
                        .delete(TEST_RECOMMENDATION_ID),
                "Author ID mismatch, editing this recommendation is not allowed for this user!");
    }

    @Test
    void testGetByFilters() {
        RecommendationFilterDto testFilterDto = new RecommendationFilterDto(
                "Test text", TEST_USER_ID, TEST_RECEIVER_ID);
        List<Recommendation> testRecommendationList = List.of(recommendation1, recommendation2, recommendation3);

        Mockito.when(recommendationRepository.findAll()).thenReturn(testRecommendationList);
        List<RecommendationDto> expectedList = testRecommendationList
                .stream().map(rec -> recommendationMapper.toRecommendationDto(rec)).toList();
        List<RecommendationDto> resultList = recommendationService.getByFilters(testFilterDto);
        assertEquals(expectedList, resultList);
    }
}
