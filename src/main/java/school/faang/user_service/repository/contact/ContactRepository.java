package school.faang.user_service.repository.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByUserIdAndType(Long userId, ContactType type);

    Optional<Contact> findByContactAndType(String contact, ContactType type);
}
