package school.faang.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Schema(description = "Ответ на операцию по запросу")
public class CountResponse {

    @Schema(description = "Количество")
    private long count;

}
