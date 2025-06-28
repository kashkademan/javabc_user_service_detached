package school.faang.user_service.repository.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.contact.ContactPreference;

public interface ContactPreferenceRepository extends JpaRepository<ContactPreference, Long> {
}