package school.faang.user_service.avatar;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * EnvironmentPostProcessor для загрузки переменных окружения из файла .env
 * и добавления их в Spring Environment перед запуском приложения.
 * <p>
 * Этот класс использует библиотеку {@link io.github.cdimascio.dotenv.Dotenv}
 * для чтения переменных из файла .env, если он существует.
 * Все переменные из .env добавляются в {@link org.springframework.core.env.Environment}
 * как источник свойств с именем "dotenv".
 * <p>
 * Позволяет использовать переменные окружения из .env файла
 * аналогично стандартным Spring-свойствам (например, в @Value или application.yml).
 * <p>
 * Если файл .env отсутствует, загрузка игнорируется без ошибок.
 * <p>
 * Регистрируется в Spring через файл
 * <code>META-INF/spring.factories</code> с ключом
 * <code>org.springframework.boot.env.EnvironmentPostProcessor</code>.
 * <p>
 * Использование данного класса позволяет удобно конфигурировать
 * приложение в локальной среде разработки или в окружениях,
 * где нет возможности задавать переменные окружения другим способом.
 *
 * @author agent
 * @since 30.07.2025
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        Map<String, Object> properties = new HashMap<>();
        dotenv.entries().forEach(entry -> properties.put(entry.getKey(), entry.getValue()));

        environment.getPropertySources().addLast(new MapPropertySource("dotenv", properties));
    }
}