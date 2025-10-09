package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationResponseDto;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filters.recommendation.RecommendationAuthorFilter;
import school.faang.user_service.filters.recommendation.RecommendationContentFilter;
import school.faang.user_service.filters.recommendation.RecommendationFilter;
import school.faang.user_service.filters.recommendation.RecommendationReceiverFilter;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * unit test for RecommendationServiceImpl.
 */

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    private final RecommendationContentFilter contentFilter = new RecommendationContentFilter();
    private final RecommendationReceiverFilter receiverFilter = new RecommendationReceiverFilter();
    private final RecommendationAuthorFilter authorFilter = new RecommendationAuthorFilter();

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserContext userContext;
    @Spy
    private final RecommendationMapper recommendationMapper = new RecommendationMapperImpl();
    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationService, "cooldownMonths", 6);
        List<RecommendationFilter> filters = List.of(
                contentFilter,
                receiverFilter,
                authorFilter
        );
        ReflectionTestUtils.setField(recommendationService, "recommendationFilters", filters);
    }

    @Nested
    class Create {

        @Test
        @DisplayName("create: save recommendation and return DTO")
        void create_success() {
            // Setup
            CreateRecommendationRequestDto input = new CreateRecommendationRequestDto(
                    42L, "Sample content", List.of(1L, 2L)
            );

            when(userContext.getUserId()).thenReturn(10L);
            when(recommendationRepository.create(10L, 42L, "Sample content")).thenReturn(100L);

            User author = User.builder().id(10L).build();
            User receiver = User.builder().id(42L).build();

            Recommendation expectedRecommendation = Recommendation.builder()
                    .id(100L)
                    .content("Sample content")
                    .author(author)
                    .receiver(receiver)
                    .build();

            when(recommendationRepository.findById(100L)).thenReturn(Optional.of(expectedRecommendation));
            when(recommendationRepository.findAll()).thenReturn(Collections.emptyList());

            RecommendationResponseDto expectedRecommendationResponseDto = new RecommendationResponseDto(
                    100L, 10L, 42L, "Sample content"
            );

            RecommendationResponseDto actualRecommendationResponseDto = recommendationService.create(input);


            assertThat(actualRecommendationResponseDto).isEqualTo(expectedRecommendationResponseDto);

            verify(recommendationRepository).findAll();
            verify(recommendationRepository).create(10L, 42L, "Sample content");
            verify(recommendationRepository).findById(100L);
            verify(recommendationMapper).toResponse(expectedRecommendation);
            verify(skillOfferRepository).create(1L, 100L);
            verify(skillOfferRepository).create(2L, 100L);
            verifyNoMoreInteractions(recommendationRepository, skillOfferRepository, recommendationMapper);
        }

        @Test
        @DisplayName("create: throws DataValidationException when receiverId is null")
        void create_fail_receiverIdNull() {

            CreateRecommendationRequestDto input = new CreateRecommendationRequestDto(
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

            CreateRecommendationRequestDto input = new CreateRecommendationRequestDto(
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
            User author = User.builder()
                    .id(100L)
                    .build();

            User receiver = User.builder()
                    .id(42L)
                    .build();

            Recommendation existingRecommendation = Recommendation.builder()
                    .content("Old content")
                    .author(author)
                    .receiver(receiver)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(recommendationRepository.findAll()).thenReturn(List.of(existingRecommendation));
            when(userContext.getUserId()).thenReturn(100L);

            CreateRecommendationRequestDto input = new CreateRecommendationRequestDto(
                    42L, "Sample content", List.of(1L, 2L)
            );
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
            CreateRecommendationRequestDto input = new CreateRecommendationRequestDto(
                    42L, "Useful Recommendation", List.of(1L, 2L)
            );

            User author = User.builder()
                    .id(100L)
                    .build();

            User receiver = User.builder()
                    .id(42L)
                    .build();

            Recommendation savedRecommendation = Recommendation.builder()
                    .id(1L)
                    .content("Useful Recommendation")
                    .author(author)
                    .receiver(receiver)
                    .build();

            when(userContext.getUserId()).thenReturn(100L);
            when(recommendationRepository.create(100L, 42L, "Useful Recommendation"))
                    .thenReturn(1L);
            when(recommendationRepository.findById(1L)).thenReturn(Optional.of(savedRecommendation));
            when(recommendationRepository.findAll()).thenReturn(Collections.emptyList());

            RecommendationResponseDto result = recommendationService.create(input);

            RecommendationResponseDto expectedResponse = new RecommendationResponseDto(
                    1L, 100L, 42L, "Useful Recommendation"
            );
            assertThat(result).isEqualTo(expectedResponse);

            verify(recommendationRepository).create(100L, 42L, "Useful Recommendation");
            verify(skillOfferRepository).create(1L, 1L);
            verify(skillOfferRepository).create(2L, 1L);
            verify(recommendationRepository).findById(1L);
            verify(recommendationRepository).findAll();
            verifyNoMoreInteractions(recommendationRepository, skillOfferRepository);
        }


        @Test
        @DisplayName("create: throws ForbiddenException when author is the same as receiver")
        void create_fail_authorEqualsReceiver() {

            long sameUserId = 100L;
            CreateRecommendationRequestDto input = new CreateRecommendationRequestDto(
                    sameUserId, "Self recommendation", List.of(1L, 2L)
            );

            when(userContext.getUserId()).thenReturn(sameUserId);

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
            User author = User.builder()
                    .id(100L)
                    .build();

            User receiver = User.builder()
                    .id(42L)
                    .build();

            Recommendation existingRecommendation = Recommendation.builder()
                    .id(recommendationId)
                    .content("Old content")
                    .author(author)
                    .receiver(receiver)
                    .build();

            Recommendation updatedRecommendation = Recommendation.builder()
                    .id(recommendationId)
                    .content("Updated content")
                    .author(author)
                    .receiver(receiver)
                    .build();

            RecommendationResponseDto expectedRecommendationResponseDto = new RecommendationResponseDto(
                    recommendationId, 100L, 42L, "Updated content"
            );

            when(userContext.getUserId()).thenReturn(100L);
            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(existingRecommendation));
            when(recommendationRepository.save(existingRecommendation)).thenReturn(updatedRecommendation);

            UpdateRecommendationRequestDto input = new UpdateRecommendationRequestDto(
                    "Updated content", null
            );
            RecommendationResponseDto result = recommendationService.update(recommendationId, input);

            assertThat(result).isEqualTo(expectedRecommendationResponseDto);
            verify(recommendationRepository).findById(recommendationId);
            verify(recommendationRepository).save(existingRecommendation);
            verify(recommendationMapper).toResponse(updatedRecommendation);
            verifyNoMoreInteractions(recommendationRepository, recommendationMapper);
        }

        @Test
        @DisplayName("update: throws DataValidationException when content is blank")
        void update_fail_contentBlank() {
            long recommendationId = 1L;
            UpdateRecommendationRequestDto input = new UpdateRecommendationRequestDto(
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
            UpdateRecommendationRequestDto input = new UpdateRecommendationRequestDto(
                    "Updated content", null
            );

            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.empty());

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
            User author = User.builder()
                    .id(200L)
                    .build();

            Recommendation existingRecommendation = Recommendation.builder()
                    .id(recommendationId)
                    .content("Old content")
                    .author(author)
                    .build();

            when(userContext.getUserId()).thenReturn(100L);
            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(existingRecommendation));

            UpdateRecommendationRequestDto input = new UpdateRecommendationRequestDto("Updated content", null);
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
            User author = User.builder()
                    .id(100L)
                    .build();

            User receiver = User.builder()
                    .id(42L)
                    .build();

            Recommendation existingRecommendation = Recommendation.builder()
                    .id(recommendationId)
                    .content("Old content")
                    .author(author)
                    .receiver(receiver)
                    .build();

            Recommendation updatedRecommendation = Recommendation.builder()
                    .id(recommendationId)
                    .content("Updated content")
                    .author(author)
                    .receiver(receiver)
                    .build();

            RecommendationResponseDto expectedRecommendation = new RecommendationResponseDto(
                    recommendationId, 100L, 42L, "Updated content"
            );

            when(userContext.getUserId()).thenReturn(100L);
            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(existingRecommendation));
            when(recommendationRepository.save(existingRecommendation)).thenReturn(updatedRecommendation);

            UpdateRecommendationRequestDto input = new UpdateRecommendationRequestDto(
                    "Updated content", List.of(1L, 2L)
            );
            RecommendationResponseDto result = recommendationService.update(recommendationId, input);

            assertThat(result).isEqualTo(expectedRecommendation);
            verify(recommendationRepository).findById(recommendationId);
            verify(recommendationRepository).save(existingRecommendation);
            verify(skillOfferRepository).deleteAllByRecommendationId(recommendationId);
            verify(skillOfferRepository).create(recommendationId, 1L);
            verify(skillOfferRepository).create(recommendationId, 2L);
            verify(recommendationMapper).toResponse(updatedRecommendation);
            verifyNoMoreInteractions(recommendationRepository, recommendationMapper, skillOfferRepository);
        }
    }

    @Nested
    class Delete {

        @Test
        @DisplayName("delete: successfully deletes recommendation owned by user")
        void delete_success() {

            long recommendationId = 1L;
            when(userContext.getUserId()).thenReturn(100L);
            when(recommendationRepository.deleteByIdAndAuthor_id(recommendationId, 100L)).thenReturn(1);

            recommendationService.delete(recommendationId);

            verify(recommendationRepository).deleteByIdAndAuthor_id(recommendationId, 100L);
            verify(skillOfferRepository).deleteAllByRecommendationId(recommendationId);
            verifyNoMoreInteractions(recommendationRepository, skillOfferRepository);
        }

        @Test
        @DisplayName("delete: throws ForbiddenException when user is not the owner")
        void delete_fail_notOwner() {

            long recommendationId = 1L;
            when(userContext.getUserId()).thenReturn(100L);
            when(recommendationRepository.deleteByIdAndAuthor_id(recommendationId, 100L)).thenReturn(0);

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
            when(userContext.getUserId()).thenReturn(100L);
            when(recommendationRepository.deleteByIdAndAuthor_id(recommendationId, 100L)).thenReturn(0);

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
            User author = User.builder()
                    .id(100L)
                    .build();

            User receiver = User.builder()
                    .id(200L)
                    .build();

            Recommendation recommendation1 = Recommendation.builder()
                    .id(1L)
                    .content("Content with keyword")
                    .author(author)
                    .receiver(receiver)
                    .build();

            Recommendation recommendation2 = Recommendation.builder()
                    .id(2L)
                    .content("Other content")
                    .author(author)
                    .receiver(receiver)
                    .build();

            RecommendationResponseDto dto1 = new RecommendationResponseDto(
                    1L, 100L, 200L, "Content with keyword"
            );
            // RecommendationResponseDto dto2 = new RecommendationResponseDto(2L, 100L, 200L, "Other content");

            when(recommendationRepository.findAll()).thenReturn(List.of(recommendation1, recommendation2));

            FilterRecommendationRequestDto filters = new FilterRecommendationRequestDto(
                    "keyword", 100L, 200L
            );
            List<RecommendationResponseDto> recommendationResponseDto = recommendationService.getByFilters(filters);

            assertThat(recommendationResponseDto).containsExactly(dto1);
            verify(recommendationRepository).findAll();
            verify(recommendationMapper).toResponse(recommendation1);
            verifyNoMoreInteractions(recommendationRepository, recommendationMapper);
        }

        @Test
        @DisplayName("getByFilters: returns all recommendations when filters are empty")
        void getByFilters_emptyFilters() {
            User author1 = User.builder()
                    .id(100L)
                    .build();

            User author2 = User.builder()
                    .id(101L)
                    .build();

            User receiver1 = User.builder()
                    .id(200L)
                    .build();

            User receiver2 = User.builder()
                    .id(201L)
                    .build();

            Recommendation recommendation1 = Recommendation.builder()
                    .id(1L)
                    .content("First recommendation")
                    .author(author1)
                    .receiver(receiver1)
                    .build();

            Recommendation recommendation2 = Recommendation.builder()
                    .id(2L)
                    .content("Second recommendation")
                    .author(author2)
                    .receiver(receiver2)
                    .build();

            when(recommendationRepository.findAll()).thenReturn(List.of(recommendation1, recommendation2));
            RecommendationResponseDto dto1 = new RecommendationResponseDto(1L, 100L, 200L, "First recommendation");
            RecommendationResponseDto dto2 = new RecommendationResponseDto(2L, 101L, 201L, "Second recommendation");

            FilterRecommendationRequestDto filters = new FilterRecommendationRequestDto(null, null, null);
            List<RecommendationResponseDto> recommendationResponseDto = recommendationService.getByFilters(filters);

            assertThat(recommendationResponseDto).containsExactlyInAnyOrder(dto1, dto2);
            verify(recommendationRepository).findAll();
            verify(recommendationMapper).toResponse(recommendation1);
            verify(recommendationMapper).toResponse(recommendation2);
            verifyNoMoreInteractions(recommendationRepository, recommendationMapper);
        }

        @Test
        @DisplayName("getByFilters: returns empty list if no recommendations match filters")
        void getByFilters_noResults() {
            FilterRecommendationRequestDto filters = new FilterRecommendationRequestDto("unmatched", 123L, 456L);

            when(recommendationRepository.findAll()).thenReturn(Collections.emptyList());

            List<RecommendationResponseDto> recommendationResponseDto = recommendationService.getByFilters(filters);

            assertThat(recommendationResponseDto).isEmpty();
            verify(recommendationRepository).findAll();
            verifyNoInteractions(recommendationMapper);
            verifyNoMoreInteractions(recommendationRepository);
        }
    }
}
