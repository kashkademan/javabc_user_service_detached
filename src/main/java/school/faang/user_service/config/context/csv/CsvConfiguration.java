package school.faang.user_service.config.context.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class CsvConfiguration {
    @Bean
    public CsvMapper csvMapper() {
        CsvMapper mapper = new CsvMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }
}
