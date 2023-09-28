package com.catalog.utils.errors;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ValidatorService {
    public void validate(@Valid Object data) {
    }
}
