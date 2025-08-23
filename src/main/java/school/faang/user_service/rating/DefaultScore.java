package school.faang.user_service.rating;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность определяющая обычные очки за "не сложные" действия
 *
 * @author Linempy
 * @since 23.08.2025
 */
@Entity
@EqualsAndHashCode
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "default_score")
public class DefaultScore {

}