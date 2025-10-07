package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.RecommendationResponse;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * unit test for RecommendationServiceImpl.
 */
class RecommendationServiceImplTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Spy
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        this.recommendationService = new RecommendationServiceImpl(
                recommendationRepository, recommendationMapper, userContext, skillOfferRepository
        );
        ReflectionTestUtils.setField(recommendationService, "cooldownMonths", 6);

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class Create {

        @Test
        @DisplayName("create: save recommendation and return DTO")
        void create_success() {

            CreateRecommendationRequest input = new CreateRecommendationRequest(
                    42L, "Sample content", List.of(1L, 2L)
            );

            Recommendation saved = new Recommendation();
            RecommendationResponse expectedDto = new RecommendationResponse(
                    100L, 10L, 42L, "Sample content"
            );

            given(userContext.getUserId()).willReturn(100L);
            given(recommendationRepository.create(100L, 42L, "Sample content")).willReturn(0L);
            given(recommendationRepository.findById(anyLong())).willReturn(Optional.of(saved));
            given(recommendationMapper.toRecommendationDto(saved)).willReturn(expectedDto);
            given(recommendationRepository.findAll()).willReturn(Collections.emptyList());

            RecommendationResponse result = recommendationService.create(input);

            assertThat(result).isEqualTo(expectedDto);

            verify(recommendationRepository).findAll();
            verify(recommendationRepository).create(100L, 42L, "Sample content");
            verify(recommendationRepository).findById(0L);
            verify(recommendationMapper).toRecommendationDto(saved);
            verify(skillOfferRepository).create(1L, 0L);
            verify(skillOfferRepository).create(2L, 0L);
            verifyNoMoreInteractions(recommendationMapper, recommendationRepository, skillOfferRepository);
        }


        @Test
        @DisplayName("create: throws DataValidationException when receiverId is null")
        void create_fail_receiverIdNull() {

            CreateRecommendationRequest input = new CreateRecommendationRequest(
                    null, "Content", List.of(1L, 2L)
            );

            assertThatThrownBy(() -> recommendationService.create(input))
                    .isInstanceOf(DataValidationException.class)
                    .hasMessage("receiverId is required");

            verifyNoInteractions(recommendationRepository, recommendationMapper, skillOfferRepository);
        }

        @Test
        @DisplayName("create: throws DataValidationException when content is blank")
        void create_fail_contentBlank() {

            CreateRecommendationRequest input = new CreateRecommendationRequest(
                    42L, "   ", List.of(1L, 2L)
            );

            assertThatThrownBy(() -> recommendationService.create(input))
                    .isInstanceOf(DataValidationException.class)
                    .hasMessage("content must not be blank");

            verifyNoInteractions(recommendationRepository, recommendationMapper, skillOfferRepository);
        }

        @Test
        @DisplayName("create: throws DataValidationException on cooldown violations")
        void create_fail_cooldownViolation() {
            CreateRecommendationRequest input = new CreateRecommendationRequest(
                    42L, "Sample content", List.of(1L, 2L)
            );

            Recommendation existing = new Recommendation();
            existing.setContent("Old content");
            User author = new User();
            author.setId(100L);
            existing.setAuthor(author);
            User receiver = new User();
            receiver.setId(42L);
            existing.setReceiver(receiver);
            existing.setCreatedAt(LocalDateTime.now()); // Set recent creation date to trigger cooldown

            given(recommendationRepository.findAll()).willReturn(List.of(existing));
            given(userContext.getUserId()).willReturn(100L);

            assertThatThrownBy(() -> recommendationService.create(input))
                    .isInstanceOf(DataValidationException.class)
                    .hasMessageContaining("You can leave a recommendation for this user only once in");

            verify(recommendationRepository).findAll();
            verifyNoInteractions(recommendationMapper, skillOfferRepository);
            verifyNoMoreInteractions(recommendationRepository);
        }


        @Test
        @DisplayName("create: handles skillIds if provided")
        void create_success_withSkillIds() {

            CreateRecommendationRequest input = new CreateRecommendationRequest(
                    42L, "Useful Recommendation", List.of(1L, 2L)
            );

            Recommendation saved = new Recommendation();
            RecommendationResponse expectedDto = new RecommendationResponse(
                    1L, 100L, 42L, "Useful Recommendation"
            );

            given(userContext.getUserId()).willReturn(100L);

            given(recommendationRepository.create(anyLong(), anyLong(), anyString())).willReturn(1L);
            given(recommendationRepository.findById(1L)).willReturn(Optional.of(saved));
            given(recommendationMapper.toRecommendationDto(saved)).willReturn(expectedDto);
            given(recommendationRepository.findAll()).willReturn(Collections.emptyList());

            RecommendationResponse result = recommendationService.create(input);

            verify(recommendationRepository).create(100L, 42L, "Useful Recommendation");
            verify(skillOfferRepository).create(1L, 1L);
            verify(skillOfferRepository).create(2L, 1L);
            assertThat(result).isEqualTo(expectedDto);
        }


        @Test
        @DisplayName("create: throws ForbiddenException when author is the same as receiver")
        void create_fail_authorEqualsReceiver() {

            long sameUserId = 100L;
            CreateRecommendationRequest input = new CreateRecommendationRequest(
                    sameUserId, "Self recommendation", List.of(1L, 2L)
            );

            given(userContext.getUserId()).willReturn(sameUserId);

            assertThatThrownBy(() -> recommendationService.create(input))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("You cannot create recommendation for yourself");

            verifyNoInteractions(recommendationRepository, recommendationMapper);
        }
    }

    @Nested
    class Update {

        @Test
        @DisplayName("update: successfully updates recommendation and returns DTO")
        void update_success() {

            long recommendationId = 1L;
            UpdateRecommendationRequest input = new UpdateRecommendationRequest(
                    "Updated content", null
            );
            Recommendation existing = new Recommendation();
            existing.setId(recommendationId);
            existing.setContent("Old content");
            User author = new User();
            author.setId(100L);
            existing.setAuthor(author);
            User receiver = new User();
            receiver.setId(42L);
            existing.setReceiver(receiver);
            Recommendation updated = new Recommendation();
            updated.setId(recommendationId);
            updated.setContent("Updated content");
            RecommendationResponse expectedDto = new RecommendationResponse(
                    recommendationId, 100L, 42L, "Updated content"
            );

            given(userContext.getUserId()).willReturn(100L);
            given(recommendationRepository.findById(recommendationId)).willReturn(Optional.of(existing));
            given(recommendationRepository.save(existing)).willReturn(updated);
            given(recommendationMapper.toRecommendationDto(updated)).willReturn(expectedDto);

            RecommendationResponse result = recommendationService.update(recommendationId, input);

            assertThat(result).isEqualTo(expectedDto);
            verify(recommendationRepository).findById(recommendationId);
            verify(recommendationRepository).save(existing);
            verify(recommendationMapper).toRecommendationDto(updated);
            verifyNoMoreInteractions(recommendationRepository, recommendationMapper);
        }

        @Test
        @DisplayName("update: throws DataValidationException when content is blank")
        void update_fail_contentBlank() {
            long recommendationId = 1L;
            UpdateRecommendationRequest input = new UpdateRecommendationRequest(
                    "   ", null
            );

            assertThatThrownBy(() -> recommendationService.update(recommendationId, input))
                    .isInstanceOf(DataValidationException.class)
                    .hasMessage("content must not be blank");

            verifyNoInteractions(recommendationRepository, recommendationMapper);
        }

        @Test
        @DisplayName("update: throws DataValidationException when recommendation not found")
        void update_fail_notFound() {

            long recommendationId = 1L;
            UpdateRecommendationRequest input = new UpdateRecommendationRequest(
                    "Updated content", null
            );

            given(recommendationRepository.findById(recommendationId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> recommendationService.update(recommendationId, input))
                    .isInstanceOf(DataValidationException.class)
                    .hasMessage("Recommendation not found: id=1");

            verify(recommendationRepository).findById(recommendationId);
            verifyNoMoreInteractions(recommendationRepository);
        }

        @Test
        @DisplayName("update: throws ForbiddenException when user tries to update another user's recommendation")
        void update_fail_forbidden() {

            long recommendationId = 1L;
            UpdateRecommendationRequest input = new UpdateRecommendationRequest(
                    "Updated content", null
            );
            Recommendation existing = new Recommendation();
            existing.setId(recommendationId);
            existing.setContent("Old content");
            User newUser = new User();
            newUser.setId(200L);
            existing.setAuthor(newUser);

            given(userContext.getUserId()).willReturn(100L);
            given(recommendationRepository.findById(recommendationId)).willReturn(Optional.of(existing));

            assertThatThrownBy(() -> recommendationService.update(recommendationId, input))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("You can update only your own recommendation");

            verify(recommendationRepository).findById(recommendationId);
            verifyNoMoreInteractions(recommendationRepository);
        }

        @Test
        @DisplayName("update: updates recommendation with skillIds and returns DTO")
        void update_success_withSkillIds() {

            long recommendationId = 1L;
            UpdateRecommendationRequest input = new UpdateRecommendationRequest(
                    "Updated content", List.of(1L, 2L)
            );
            Recommendation existing = new Recommendation();
            existing.setId(recommendationId);
            existing.setContent("Old content");
            User author = new User();
            author.setId(100L);
            existing.setAuthor(author);
            User receiver = new User();
            receiver.setId(42L);
            existing.setReceiver(receiver);

            Recommendation updated = new Recommendation();
            updated.setId(recommendationId);
            updated.setContent("Updated content");
            RecommendationResponse expectedDto = new RecommendationResponse(
                    recommendationId, 100L, 42L, "Updated content"
            );

            given(userContext.getUserId()).willReturn(100L);
            given(recommendationRepository.findById(recommendationId)).willReturn(Optional.of(existing));
            given(recommendationRepository.save(existing)).willReturn(updated);
            given(recommendationMapper.toRecommendationDto(updated)).willReturn(expectedDto);

            RecommendationResponse result = recommendationService.update(recommendationId, input);

            assertThat(result).isEqualTo(expectedDto);
            verify(recommendationRepository).findById(recommendationId);
            verify(recommendationRepository).save(existing);
            verify(skillOfferRepository).deleteAllByRecommendationId(recommendationId);
            verify(skillOfferRepository).create(recommendationId, 1L);
            verify(skillOfferRepository).create(recommendationId, 2L);
            verify(recommendationMapper).toRecommendationDto(updated);
            verifyNoMoreInteractions(recommendationRepository, recommendationMapper, skillOfferRepository);
        }

    }

    @Nested
    class Delete {

        @Test
        @DisplayName("delete: successfully deletes recommendation owned by user")
        void delete_success() {

            long recommendationId = 1L;
            given(userContext.getUserId()).willReturn(100L);
            given(recommendationRepository.deleteByIdAndAuthor_id(recommendationId, 100L)).willReturn(1);

            recommendationService.delete(recommendationId);

            verify(recommendationRepository).deleteByIdAndAuthor_id(recommendationId, 100L);
            verify(skillOfferRepository).deleteAllByRecommendationId(recommendationId);
            verifyNoMoreInteractions(recommendationRepository, skillOfferRepository);
        }

        @Test
        @DisplayName("delete: throws ForbiddenException when user is not the owner")
        void delete_fail_notOwner() {

            long recommendationId = 1L;
            given(userContext.getUserId()).willReturn(100L);
            given(recommendationRepository.deleteByIdAndAuthor_id(recommendationId, 100L)).willReturn(0);

            assertThatThrownBy(() -> recommendationService.delete(recommendationId))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("You can delete only your own recommendation or it does not exist");

            verify(recommendationRepository).deleteByIdAndAuthor_id(recommendationId, 100L);
            verifyNoInteractions(skillOfferRepository);
            verifyNoMoreInteractions(recommendationRepository);
        }

        @Test
        @DisplayName("delete: no exception thrown for non-existing recommendation")
        void delete_fail_recommendationNotFound() {

            long recommendationId = 1L;
            given(userContext.getUserId()).willReturn(100L);
            given(recommendationRepository.deleteByIdAndAuthor_id(recommendationId, 100L)).willReturn(0);

            assertThatThrownBy(() -> recommendationService.delete(recommendationId))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("You can delete only your own recommendation or it does not exist");

            verify(recommendationRepository).deleteByIdAndAuthor_id(recommendationId, 100L);
            verifyNoInteractions(skillOfferRepository);
            verifyNoMoreInteractions(recommendationRepository);
        }
    }

    @Nested
    class GetByFilters {

        @Test
        @DisplayName("getByFilters: successfully filters recommendations based on criteria")
        void getByFilters_success() {
            Recommendation recommendation1 = new Recommendation();
            Recommendation recommendation2 = new Recommendation();
            User author = new User();
            author.setId(100L);
            recommendation1.setAuthor(author);
            recommendation2.setAuthor(author);
            recommendation1.setContent("Content with keyword");
            recommendation2.setContent("Other content");
            User receiver = new User();
            receiver.setId(200L);
            recommendation1.setReceiver(receiver);
            recommendation2.setReceiver(receiver);

            RecommendationResponse dto1 = new RecommendationResponse(1L, 100L, 200L, "Content with keyword");
            RecommendationResponse dto2 = new RecommendationResponse(2L, 100L, 200L, "Other content");

            FilterRecommendationRequest filters = new FilterRecommendationRequest("keyword", 100L, 200L);

            given(recommendationRepository.findAll()).willReturn(List.of(recommendation1, recommendation2));
            given(recommendationMapper.toRecommendationDto(recommendation1)).willReturn(dto1);
            given(recommendationMapper.toRecommendationDto(recommendation2)).willReturn(dto2);

            List<RecommendationResponse> result = recommendationService.getByFilters(filters);

            assertThat(result).containsExactly(dto1);
            verify(recommendationRepository).findAll();
            verify(recommendationMapper).toRecommendationDto(recommendation1);
            verifyNoMoreInteractions(recommendationRepository, recommendationMapper);
        }

        @Test
        @DisplayName("getByFilters: returns all recommendations when filters are empty")
        void getByFilters_emptyFilters() {
            Recommendation recommendation1 = new Recommendation();
            Recommendation recommendation2 = new Recommendation();
            User author1 = new User();
            author1.setId(100L);
            User author2 = new User();
            author2.setId(101L);
            recommendation1.setAuthor(author1);
            recommendation2.setAuthor(author2);
            recommendation1.setContent("First recommendation");
            recommendation2.setContent("Second recommendation");

            RecommendationResponse dto1 = new RecommendationResponse(1L, 100L, 200L, "First recommendation");
            RecommendationResponse dto2 = new RecommendationResponse(2L, 101L, 201L, "Second recommendation");

            FilterRecommendationRequest filters = new FilterRecommendationRequest(null, null, null);

            given(recommendationRepository.findAll()).willReturn(List.of(recommendation1, recommendation2));
            given(recommendationMapper.toRecommendationDto(recommendation1)).willReturn(dto1);
            given(recommendationMapper.toRecommendationDto(recommendation2)).willReturn(dto2);

            List<RecommendationResponse> result = recommendationService.getByFilters(filters);

            assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
            verify(recommendationRepository).findAll();
            verify(recommendationMapper).toRecommendationDto(recommendation1);
            verify(recommendationMapper).toRecommendationDto(recommendation2);
            verifyNoMoreInteractions(recommendationRepository, recommendationMapper);
        }

        @Test
        @DisplayName("getByFilters: returns empty list if no recommendations match filters")
        void getByFilters_noResults() {
            FilterRecommendationRequest filters = new FilterRecommendationRequest("unmatched", 123L, 456L);

            given(recommendationRepository.findAll()).willReturn(Collections.emptyList());

            List<RecommendationResponse> result = recommendationService.getByFilters(filters);

            assertThat(result).isEmpty();
            verify(recommendationRepository).findAll();
            verifyNoInteractions(recommendationMapper);
            verifyNoMoreInteractions(recommendationRepository);
        }
    }
}
