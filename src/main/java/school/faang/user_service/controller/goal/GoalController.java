package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.goal.filter.GoalFilterDto;
import school.faang.user_service.dto.goal.request.CreateGoalDto;
import school.faang.user_service.dto.goal.request.UpdateGoalDto;
import school.faang.user_service.dto.goal.response.GoalDto;

import java.util.List;

@Tag(name = "Goals", description = "Управление целями")
public interface GoalController {

    @Operation(
            summary = "Получить цель по ID",
            description = "Возвращает информацию о цели по её идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цель найдена"),
            @ApiResponse(responseCode = "404", description = "Цель не найдена")
    })
    ResponseEntity<GoalDto> getGoal(
            @Parameter(description = "ID цели", example = "1")
            long goalId);

    @Operation(
            summary = "Получить фильтрованный список целей",
            description = "Возвращает список целей, который содержит цели удовлетворяющие переданному фильтру",
            requestBody = @RequestBody(
                    description = "JSON с параметрами фильтра",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = GoalFilterDto.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получен список целей")
    })
    ResponseEntity<List<GoalDto>> getGoals(GoalFilterDto goalFilterDto);

    @Operation(
            summary = "Получить фильтрованный список дочерних целей",
            description = """
                    Возвращает список дочерних целей заданной цели,
                    который содержит цели удовлетворяющие переданному фильтру""",

            requestBody = @RequestBody(
                    description = "JSON с параметрами фильтра",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = GoalFilterDto.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получен список дочерних целей"),
            @ApiResponse(responseCode = "404", description = "Цель не найдена")

    })
    ResponseEntity<List<GoalDto>> getSubGoals(
            @Parameter(description = "ID цели родителя", example = "1")
            long parentId,
            GoalFilterDto goalFilterDto
    );

    @Operation(
            summary = "Создать новую цель",
            description = "Создает новую цель с заданными параметрами и назначает текущему пользователю",
            requestBody = @RequestBody(
                    description = "JSON с параметрами цели",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateGoalDto.class)
                    )
            ),
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            in = ParameterIn.HEADER,
                            description = "ID Пользователя",
                            required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Цель создана"),
            @ApiResponse(responseCode = "400", description = "Параметры цели не валидные"),
            @ApiResponse(responseCode = "401", description = "Не задан id пользователя"),
            @ApiResponse(responseCode = "404", description = "Передан не существующий навык"),
            @ApiResponse(responseCode = "412", description = "Пользователь имеет максимальное количество активных целей")
    })
    ResponseEntity<GoalDto> createGoal(CreateGoalDto goalDto);

    @Operation(
            operationId = "updateGoalId",
            summary = "Обновить заданную цель",
            description = "Обновить существующую цель заданными параметрами",
            requestBody = @RequestBody(
                    description = "JSON с новыми параметрами цели",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateGoalDto.class)
                    )
            ),
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            in = ParameterIn.HEADER,
                            description = "ID Пользователя",
                            required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цель обновлена"),
            @ApiResponse(responseCode = "400", description = "Параметры цели не валидные"),
            @ApiResponse(responseCode = "401", description = "Не задан id пользователя"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является владельцем цели"),
            @ApiResponse(responseCode = "404", description = "Цель не найдена или передан не существующий навык"),
            @ApiResponse(responseCode = "412", description = "Не выполнено условие обновления цели")
    })

    ResponseEntity<GoalDto> updateGoal(
            @Parameter(description = "ID цели", example = "1")
            long goalId,
            UpdateGoalDto goalDto);

    @Operation(
            summary = "Удалить заданную цель",
            description = "Удаляет связь пользователя с целью и цель, если у нее не осталось владельцев",
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            in = ParameterIn.HEADER,
                            description = "ID Пользователя",
                            required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Цель удалена"),
            @ApiResponse(responseCode = "401", description = "Не задан id пользователя"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является владельцем цели"),
            @ApiResponse(responseCode = "404", description = "Цель не найдена")
    })
    ResponseEntity<Void> deleteGoal(
            @Parameter(description = "ID цели", example = "1")
            long goalId);
}