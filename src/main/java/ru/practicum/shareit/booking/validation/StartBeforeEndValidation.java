package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingInputDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDateTime;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class StartBeforeEndValidation implements ConstraintValidator<StartBeforeEnd, BookingInputDto> {

    @Override
    public boolean isValid(BookingInputDto booking, ConstraintValidatorContext context) {
        if (booking.getStartTime() == null || booking.getEndTime() == null || booking.getItemId() == null) {
            return false;
        }
        if (!(booking.getStartTime() instanceof LocalDateTime)
                || !(booking.getEndTime() instanceof LocalDateTime)) {
            throw new IllegalArgumentException("Недопустимая сигнатура метода");
        }

        return (booking.getStartTime().isAfter(LocalDateTime.now())
                && booking.getStartTime().isBefore(booking.getEndTime()));
    }
}
