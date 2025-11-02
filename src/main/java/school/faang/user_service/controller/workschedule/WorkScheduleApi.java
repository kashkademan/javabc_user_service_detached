package school.faang.user_service.controller.workschedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;

@Tag(name = "Work Schedule Controller", description = "APIs for managing work schedules")
public interface WorkScheduleApi {

    @Operation(
            summary = "Add work schedule",
            description = "Creates a new work schedule entry."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Work schedule successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    WorkScheduleDto addWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto);

    @Operation(
            summary = "Update work schedule",
            description = "Updates an existing work schedule entry."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Work schedule successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Work schedule not found")
    })
    WorkScheduleDto updateWorkSchedule(long workScheduleId, WorkScheduleUpdateDto workScheduleUpdateDto);

    @Operation(
            summary = "Get work schedule by ID",
            description = "Retrieves a work schedule entry by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Work schedule was successfully received"),
            @ApiResponse(responseCode = "404", description = "Work schedule not found")
    })
    WorkScheduleDto getById(long workScheduleId);

    @Operation(
            summary = "Delete work schedule",
            description = "Deletes a work schedule entry by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Work schedule successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Work schedule not found")
    })
    void deleteWorkSchedule(long workScheduleId);
}
