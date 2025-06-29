package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.service.CountryService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {

    @Mock
    private CountryRepository repository;

    @InjectMocks
    private CountryService countryService;

    @Test
    void testGetCountryByIdEntityNotFound() {
        long id = -1L;

        assertThrows(EntityNotFoundException.class, () -> countryService.getCountryById(id));
    }

    @Test
    void testGetCountryById() {
        long id = 1L;
        String title = "asdf";
        Country country = createCountry(id, title);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(country));

        Country result = countryService.getCountryById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    private Country createCountry(long id, String title) {
        return Country.builder()
                .id(id)
                .title(title)
                .residents(new ArrayList<>())
                .build();
    }
}
