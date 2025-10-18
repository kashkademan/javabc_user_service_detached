package school.faang.user_service.config.context;

import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI userServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .description("API для управления пользователями в FAANG School")
                        .version("1.0")
                        .contact(new Contact()
                                .name("FAANG Team")
                                .email("support@faang.school")
                                .url("https://faang.school"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}