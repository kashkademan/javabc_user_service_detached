package school.faang.user_service.repository.country;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.country.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}