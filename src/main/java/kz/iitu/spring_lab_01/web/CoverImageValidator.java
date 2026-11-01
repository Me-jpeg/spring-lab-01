package kz.iitu.spring_lab_01.web;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Locale;

public class CoverImageValidator implements ConstraintValidator<CoverImage, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null || value.isBlank()) {
            return true; // optional field: empty is valid
        }
        String lower = value.toLowerCase(Locale.ROOT);
        boolean schemeOk = lower.startsWith("http://") || lower.startsWith("https://");
        if (!schemeOk) {
            return false;
        }
        String path = lower.split("[?#]", 2)[0];
        return path.endsWith(".jpg") || path.endsWith(".jpeg")
                || path.endsWith(".png") || path.endsWith(".gif")
                || path.endsWith(".webp");
    }
}
