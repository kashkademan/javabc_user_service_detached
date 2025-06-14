package school.faang.user_service.service.country;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.exception.country.CountryNotFoundException;
import school.faang.user_service.repository.country.CountryRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {
    private final CountryRepository countryRepository;

    public Country getCountryById(long countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> {
                    log.error("Country with id {} not found", countryId);
                    return new CountryNotFoundException(countryId);
                });
    }
}
