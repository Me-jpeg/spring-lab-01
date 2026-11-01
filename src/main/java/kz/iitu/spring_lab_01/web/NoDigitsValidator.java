package kz.iitu.spring_lab_01.web;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoDigitsValidator implements ConstraintValidator<NoDigits, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) {
            return true; // null checks are @NotBlank's business
        }
        return value.chars().noneMatch(Character::isDigit);
    }
}
