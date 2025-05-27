package school.faang.user_service.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pagination")
@Data
public class PaginationProperties {

    @NotNull
    private Integer defaultPage;

    @NotNull
    private Integer defaultSize;
}
