package kz.iitu.spring_lab_01.web;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Variant 5: cover image URL must use http/https and end with an image extension.
 * A blank value passes (field is optional) — blank checks would be @NotBlank's business.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CoverImageValidator.class)
public @interface CoverImage {

    String message() default "{book.cover.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
