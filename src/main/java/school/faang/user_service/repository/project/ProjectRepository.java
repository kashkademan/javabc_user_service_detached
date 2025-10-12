package school.faang.user_service.repository.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.project.Project;
import school.faang.user_service.exception.EntityNotFoundException;


public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM project p
            WHERE p.id = :projectId AND p.user_id = :ownerId
            """)
    int deleteById(long ownerId, long projectId);

    boolean existsByOwnerIdAndTitleIgnoreCase(long ownerId, String title);

    default Project getByIdOrThrow(long id) {
        return findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Project %d not found", id))
                );
    }
}
