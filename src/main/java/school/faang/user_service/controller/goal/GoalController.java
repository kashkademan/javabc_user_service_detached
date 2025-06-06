package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalErrorResponseDto;
import school.faang.user_service.dto.goal.GoalIdDto;
import school.faang.user_service.dto.goal.GoalRequest;
import school.faang.user_service.dto.goal.GoalResponse;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.enums.GoalAction;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.service.goal.GoalServiceImpl;
import school.faang.user_service.util.Util;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;
import school.faang.user_service.validator.goal.request.GoalRequestValidationParams;

import java.util.ArrayList;
import java.util.List;

import static school.faang.user_service.enums.ErrorCode.JSON_PROCESSING_ERROR;
import static school.faang.user_service.enums.ErrorCode.JSON_STRUCT_ERROR;
import static school.faang.user_service.enums.ErrorCode.REQUEST_VALIDATION_ERROR;
import static school.faang.user_service.enums.ErrorCode.UNEXPECTED_ERROR;
import static school.faang.user_service.enums.ErrorCode.VALIDATION_REQUIRED;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
public class GoalController {
    private final GoalServiceImpl goalService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Validator<GoalRequest, GoalRequestValidationParams>> validators;

    //todo не сделаны эндпоинты: получения целей + фильтры; завершения цели

    @PostMapping
    public ResponseEntity<GoalResponse> createGoalHandler(@RequestBody JsonNode rawRequest) throws JsonProcessingException {
        GoalCreateDto goalCreateRq = objectMapper.treeToValue(rawRequest, GoalCreateDto.class);
        return response(() -> createGoal(goalCreateRq), HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{goalId}")
    public ResponseEntity<GoalResponse> updateGoalHandler(@PathVariable Long goalId, @RequestBody JsonNode rawRequest) throws JsonProcessingException {
        GoalDto goalUpdateRq = objectMapper.treeToValue(rawRequest, GoalDto.class);

        return response(() -> {
            List<Violation> violations = new ArrayList<>();

            if (goalId == null) {
                violations.add(new Violation(VALIDATION_REQUIRED,
                        new ErrorField("goalId", "query", null, "positive integer")));
            }

            violations.addAll(validateGoal(goalUpdateRq, "", GoalAction.UPDATE_GOAL));

            if (!violations.isEmpty()) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, REQUEST_VALIDATION_ERROR, violations);
            }

            return goalService.updateGoal(goalId, goalUpdateRq, rawRequest);
        }, HttpStatus.ACCEPTED);
    }

    @DeleteMapping(path = "/{goalId}")
    public ResponseEntity<GoalResponse> deleteGoalHandler(@PathVariable Long goalId) {
        return response(() -> {

            if (goalId == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, REQUEST_VALIDATION_ERROR, List.of(
                        new Violation(VALIDATION_REQUIRED,
                                new ErrorField("goalId", "query", null, "positive integer"))));
            }

            return goalService.deleteGoal(goalId);
        }, HttpStatus.OK);
    }

    private GoalIdDto createGoal(GoalCreateDto goalCreateRq) {
        List<Violation> violations = new ArrayList<>();

        GoalRequestValidationParams validationParams = new GoalRequestValidationParams("", GoalAction.CREATE_GOAL);

        validators.stream()
                .filter(validator -> validator.applicable(goalCreateRq, validationParams))
                .forEach(validator -> {
                    ValidationResult validationResult = validator.validate(goalCreateRq, validationParams);
                    if (!validationResult.isValid()) {
                        violations.addAll(validationResult.violations());
                    }
                });

        violations.addAll(validateGoal(goalCreateRq.getGoal(), "goal", GoalAction.CREATE_GOAL));

        if (!violations.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, REQUEST_VALIDATION_ERROR, violations);
        }

        return goalService.createGoal(goalCreateRq);
    }

    private List<Violation> validateGoal(GoalDto goal, String path, GoalAction action) {
        List<Violation> violations = new ArrayList<>();

        if (goal == null) {
            violations.add(new Violation(VALIDATION_REQUIRED,
                    new ErrorField(path, "body", null, "goal object")));
        } else {
            GoalRequestValidationParams validationParams = new GoalRequestValidationParams(path, action);

            validators.stream()
                    .filter(validator -> validator.applicable(goal, validationParams))
                    .forEach(validator -> {
                        ValidationResult validationResult = validator.validate(goal, validationParams);

                        if (!validationResult.isValid()) {
                            violations.addAll(validationResult.violations());
                        }
                    });

            if (goal.getSubGoals() != null) {
                for (int i = 0; i < goal.getSubGoals().size(); i++) {
                    violations.addAll(validateGoal(goal.getSubGoals().get(i), path + ".subGoals[" + i + "]", GoalAction.SUB_GOAL));
                }
            }
        }

        return violations;
    }

    @ExceptionHandler(BusinessException.class)
    private ResponseEntity<GoalResponse> errorResponse(BusinessException be) {
        return ResponseEntity.status(be.getStatus()).body(new GoalErrorResponseDto(be));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<GoalResponse> errorResponse(HttpMessageNotReadableException hmnre) {
        log.error(hmnre.getMessage(), hmnre);
        return errorResponse(new BusinessException(HttpStatus.BAD_REQUEST, JSON_STRUCT_ERROR));
    }

    @ExceptionHandler(JsonProcessingException.class)
    private ResponseEntity<GoalResponse> errorResponse(JsonProcessingException jpe) {
        log.error(jpe.getMessage(), jpe);
        return errorResponse(new BusinessException(HttpStatus.BAD_REQUEST, JSON_PROCESSING_ERROR));
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<GoalResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return errorResponse(new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR));
    }

    private ResponseEntity<GoalResponse> response(Util.ThrowingSupplier<GoalResponse, BusinessException> sup, HttpStatus status) {
        return ResponseEntity.status(status).body(sup.get());
    }
}
