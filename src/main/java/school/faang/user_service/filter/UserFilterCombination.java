package school.faang.user_service.filter;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
@Data
public class UserFilterCombination {
    private final List<UserFilterStrategy> userFilterStrategies;
}
