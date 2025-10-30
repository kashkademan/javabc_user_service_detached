package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private RecommendationServiceImpl service;

    @BeforeEach
    void setup() {
        try {
            var f = RecommendationServiceImpl.class.getDeclaredField("limit");
            f.setAccessible(true);
            f.setInt(service, 6);
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("create: успех")
    void create_success() {
        given(userContext.getUserId()).willReturn(1L);
        given(userRepository.existsByIdIn(List.of(1L, 2L))).willReturn(false);
        given(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .willReturn(Optional.empty());
        given(recommendationRepository.create(1L, 2L, "hi")).willReturn(100L);

        var dto = service.create(new CreateRecommendationDto(2L, "hi"));

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getAuthorId()).isEqualTo(1L);
        assertThat(dto.getReceiverId()).isEqualTo(2L);
        assertThat(dto.getContent()).isEqualTo("hi");
    }

    @Test
    @DisplayName("create: self-recommendation — ошибка")
    void create_selfRecommendation_error() {
        given(userContext.getUserId()).willReturn(2L);

        assertThatThrownBy(() -> service.create(new CreateRecommendationDto(2L, "hi")))
                .isInstanceOf(DataValidationException.class);
    }

    @Test
    @DisplayName("create: отсутствует пользователь — ошибка")
    void create_userNotFound_error() {
        given(userContext.getUserId()).willReturn(1L);
        given(userRepository.existsByIdIn(List.of(1L, 2L))).willReturn(true);

        assertThatThrownBy(() -> service.create(new CreateRecommendationDto(2L, "hi")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("create: частые рекомендации — ошибка")
    void create_tooOften_error() {
        given(userContext.getUserId()).willReturn(1L);
        given(userRepository.existsByIdIn(List.of(1L, 2L))).willReturn(false);
        Recommendation recent = new Recommendation();
        recent.setCreatedAt(LocalDateTime.now());
        given(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .willReturn(Optional.of(recent));

        assertThatThrownBy(() -> service.create(new CreateRecommendationDto(2L, "hi")))
                .isInstanceOf(DataValidationException.class);
    }

    @Test
    @DisplayName("update: успех")
    void update_success() {
        given(userContext.getUserId()).willReturn(1L);
        Recommendation rec = new Recommendation();
        User author = new User(); author.setId(1L);
        User receiver = new User(); receiver.setId(2L);
        rec.setAuthor(author); rec.setReceiver(receiver);
        given(recommendationRepository.findById(10L)).willReturn(Optional.of(rec));

        var dto = service.update(new UpdateRecommendationDto(10L, "updated"));

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getAuthorId()).isEqualTo(1L);
        assertThat(dto.getReceiverId()).isEqualTo(2L);
        assertThat(dto.getContent()).isEqualTo("updated");
    }

    @Test
    @DisplayName("update: не автор — ошибка доступа")
    void update_forbidden_error() {
        given(userContext.getUserId()).willReturn(9L);
        Recommendation rec = new Recommendation();
        User author = new User(); author.setId(1L);
        rec.setAuthor(author);
        given(recommendationRepository.findById(10L)).willReturn(Optional.of(rec));

        assertThatThrownBy(() -> service.update(new UpdateRecommendationDto(10L, "updated")))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("update: не найдено — ошибка")
    void update_notFound_error() {
        given(recommendationRepository.findById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(new UpdateRecommendationDto(10L, "updated")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("delete: успех")
    void delete_success() {
        given(userContext.getUserId()).willReturn(1L);
        given(recommendationRepository.findAuthorIdById(10L)).willReturn(Optional.of(1L));

        service.delete(10L);

        verify(recommendationRepository).deleteByIdAndAuthor_id(10L, 1L);
    }

    @Test
    @DisplayName("delete: не автор — ошибка доступа")
    void delete_forbidden_error() {
        given(userContext.getUserId()).willReturn(2L);
        given(recommendationRepository.findAuthorIdById(10L)).willReturn(Optional.of(1L));

        assertThatThrownBy(() -> service.delete(10L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("delete: не найдено — ошибка")
    void delete_notFound_error() {
        given(recommendationRepository.findAuthorIdById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(10L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getByFilters: успех")
    void getByFilters_success() {
        RecommendationFilterDto filters = new RecommendationFilterDto(null, 1L, 2L);
        List<RecommendationDto> dtos = List.of(
                RecommendationDto.builder().id(1L).authorId(1L).receiverId(2L).content("c1").build()
        );

        given(recommendationRepository.getByFilters(null, 2L, 1L)).willReturn(dtos);

        var result = service.getByFilters(filters);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }
}


