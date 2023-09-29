package com.catalog.utils.server;

import com.catalog.utils.StringTools;
import com.catalog.utils.errors.SimpleError;
import com.catalog.utils.errors.UnauthorizedError;
import com.catalog.utils.errors.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ErrorHandlingControllerAdvice {

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onConstraintValidationException(ConstraintViolationException e) {
        ValidationError error = new ValidationError();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            error.addPath(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return error.toJson();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationError error = new ValidationError();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.addPath(
                    fieldError.getField(),
                    StringTools.notNull(fieldError.getDefaultMessage(), "Invalid")
            );
        }
        return error.toJson();
    }

    @ExceptionHandler(SimpleError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onSimpleError(SimpleError error) {
        return error.toJson();
    }

    @ExceptionHandler(ValidationError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    String onValidationError(ValidationError error) {
        return error.toJson();
    }

    @ExceptionHandler(UnauthorizedError.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    String onUnauthorizedError(UnauthorizedError error) {
        return error.toJson();
    }
}