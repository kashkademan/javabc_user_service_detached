package school.faang.user_service.config.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "UserService API",
                description = "API для сервиса управления пользователями",
                version = "v1.0",
                contact = @Contact(
                        name = "Evgeniy",
                        email = "dontSendMeSpam@faang.school",
                        url = "https://faang-school.com/courses"
                )
        )
)
public class OpenApiConfig {
}