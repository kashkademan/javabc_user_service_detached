package school.faang.user_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import school.faang.user_service.validation.impl.EndDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EndDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEndDate {
    String message() default "Дата окончания должна быть позже даты начала";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
