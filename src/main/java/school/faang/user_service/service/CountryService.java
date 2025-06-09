package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.CountryRepository;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public Country getCountryByID(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no country with id = " + id));
    }
}
