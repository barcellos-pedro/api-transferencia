package com.itau.transferencia.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotBlank
@Pattern(regexp = "^\\d{5}-\\d$") // Exemplo: xxxxx-x, 00001-1, etc
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Documented
@ReportAsSingleViolation
public @interface AccountNumber {
    String message() default "must be 5 digits, a hyphen, and 1 digit (7 total)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
