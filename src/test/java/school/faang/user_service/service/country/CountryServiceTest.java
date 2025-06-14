package school.faang.user_service.service.country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.exception.country.CountryNotFoundException;
import school.faang.user_service.repository.country.CountryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {
    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private CountryService countryService;
    private Country country;

    @BeforeEach
    public void setUp() {
        country = new Country();
        country.setId(7L);
    }

    @Test
    public void testGetCountryById_successfully() {
        when(countryRepository.findById(country.getId())).thenReturn(Optional.of(country));

        Country returncountry = countryService.getCountryById(country.getId());

        verify(countryRepository, times(1)).findById(country.getId());
        assertEquals(country.getId(), returncountry.getId());
    }

    @Test
    public void testGetCountryById_countryNotFound() {
        when(countryRepository.findById(country.getId())).thenReturn(Optional.empty());

        assertThrows(CountryNotFoundException.class, () -> countryService.getCountryById(country.getId()));
        verify(countryRepository, times(1)).findById(country.getId());
    }
}
