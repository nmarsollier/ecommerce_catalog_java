package com.catalog.security;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidateAdminUser.Validator.class})
public @interface ValidateAdminUser {
    String message() default "Unauthorized";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Service
    class Validator implements ConstraintValidator<ValidateAdminUser, String> {

        @Autowired
        TokenService tokenService;

        @Override
        public boolean isValid(String header, ConstraintValidatorContext context) {
            tokenService.validateAdmin(header);
            return true;
        }
    }
}