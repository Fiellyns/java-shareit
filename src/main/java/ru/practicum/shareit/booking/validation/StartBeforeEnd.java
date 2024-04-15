package ru.practicum.shareit.booking.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = StartBeforeEndValidation.class)
@Target(ElementType.TYPE)
@Documented
public @interface StartBeforeEnd {

    String message() default "StartTime не должен быть после endTime";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
