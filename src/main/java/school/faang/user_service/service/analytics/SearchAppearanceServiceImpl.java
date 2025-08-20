package school.faang.user_service.service.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.analytics.SearchAppearanceCreateDto;
import school.faang.user_service.dto.analytics.SearchAppearanceViewDto;
import school.faang.user_service.mapper.analytics.SearchAppearanceMapper;
import school.faang.user_service.repository.analytics.SearchAppearanceRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

/**
 * Реализация сервиса для работы с аналитикой поисковых появлений
 * {@link school.faang.user_service.entity.analytics.SearchAppearance}.
 * <p>Отвечает за:</p>
 * <ul>
 *     <li>Сохранение факта того, что один пользователь искал другого
 *         ({@link #addSearchAppearance(SearchAppearanceCreateDto)}).</li>
 *     <li>Получение истории поисковых появлений для конкретного пользователя
 *         ({@link #getUserSearchAppearance(Long, int, int)}).</li>
 * </ul>
 *
 * <p>Внутри себя использует:</p>
 * <ul>
 *     <li>{@link SearchAppearanceRepository} — для работы с таблицей search_appearances.</li>
 *     <li>{@link UserRepository} — для валидации и получения пользователей по их идентификаторам.</li>
 *     <li>{@link SearchAppearanceMapper} — для преобразования между entity и DTO.</li>
 * </ul>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SearchAppearanceServiceImpl implements SearchAppearanceService {
    private final SearchAppearanceRepository searchAppearanceRepo;
    private final UserRepository userRepo;
    private final SearchAppearanceMapper mapper;

    @Override
    public void addSearchAppearance(SearchAppearanceCreateDto visit) {
        log.info("add searchAppearance {}", visit);
        var searcher = userRepo.getByIdOrThrow(visit.searcherId());
        var searched = userRepo.getByIdOrThrow(visit.searchedId());
        var entity = mapper.toEntity(visit);
        log.info("entity: {}", entity);
        entity.setSearcher(searcher);
        entity.setSearched(searched);
        searchAppearanceRepo.save(entity);
    }

    @Override
    public List<SearchAppearanceViewDto> getUserSearchAppearance(Long visitedId, int limit, int page) {
        var pageable = PageRequest.of(page, limit);
        var result = searchAppearanceRepo.findAllBySearchedIdOrderBySearchedAtDesc(visitedId, pageable);
        return mapper.toDtoList(result.getContent());
    }
}
