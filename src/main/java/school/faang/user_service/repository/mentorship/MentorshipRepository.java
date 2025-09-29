package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {

    default User getByIdOrThrow(Long userId) {
        return findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %d not found", userId)));
    }

    @Query("""
            select m from User u
            join u.mentors m
            where u.id = :userId
            """)
    List<User> getMentorsById(@Param("userId") Long userId);

    @Query("""
            select m from User u
            join u.mentees m
            where u.id = :userId
            """)
    List<User> getMenteesById(@Param("userId") Long userId);
}
