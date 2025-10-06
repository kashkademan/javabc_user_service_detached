package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("""
            select count(m) > 0 from User u
            join u.mentors m
            where u.id = :menteeId and m.id = :mentorId
            """)
    boolean existsMentorship(@Param("mentorId") Long mentorId, @Param("menteeId") Long menteeId);

    @Modifying
    @Query(value = "insert into mentorship (mentee_id, mentor_id) values (:menteeId, :mentorId)", nativeQuery = true)
    void addMentorshipNative(@Param("mentorId") Long mentorId, @Param("menteeId") Long menteeId);

    @Modifying
    @Query(value = "delete from mentorship where mentee_id = :menteeId and mentor_id = :mentorId", nativeQuery = true)
    void deleteMentorshipNative(@Param("mentorId") Long mentorId, @Param("menteeId") Long menteeId);
}
