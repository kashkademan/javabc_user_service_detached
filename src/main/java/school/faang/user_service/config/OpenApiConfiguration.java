package school.faang.user_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Corporation X - User service",
                version = "1.0.0",
                description = "API for user_service"))
@Configuration
public class OpenApiConfiguration {
}
