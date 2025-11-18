package school.faang.user_service.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM event e
            WHERE e.user_id = :userId
            """)
    List<Event> findAllByUserId(long userId);

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM event e
            JOIN user_event ue ON ue.event_id = e.id
            WHERE ue.user_id = :userId
            """)
    List<Event> findParticipatedEventsByUserId(long userId);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM event e
            WHERE e.id = :eventId AND e.user_id = :userId
            """)
    int deleteById(long userId, long eventId);

    default Event getByIdOrThrow(long eventId) {
        return findById(eventId)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Event %d not found", eventId))
                );
    }

    @Query("""
                SELECT DISTINCT e FROM Event e
                LEFT JOIN e.attendees a
                WHERE e.title LIKE %:titleContains%
                  AND e.description LIKE %:descriptionContains%
                  AND e.type = :type
                  AND (:ownerId IS NULL OR e.owner.id = :ownerId)
                  AND (:participantId IS NULL OR a.id = :participantId)
            """)
    List<Event> findEventsByFilters(@Param("titleContains") String titleContains,
                                    @Param("descriptionContains") String descriptionContains,
                                    @Param("type") EventType type,
                                    @Param("ownerId") Long ownerId,
                                    @Param("participantId") Long participantId,
                                    Pageable pageable);

    @Query(value = """
            SELECT * FROM event 
            WHERE status = 0 
              AND start_date >= now() 
              AND start_date <= now() + INTERVAL '25 hours'
            """,
            nativeQuery = true)
    List<Event> findEventsFor24HourReminder();
    @Modifying
    @Query(value = """
            DELETE FROM event
            WHERE id IN (
                SELECT id FROM event
                WHERE end_date < :cutoffDate 
                LIMIT :batchSize
            )
            """, nativeQuery = true)
    Integer deleteExpiredEventsBatch(@Param("cutoffDate") LocalDateTime cutoffDate,
                                     @Param("batchSize") int batchSize);
}