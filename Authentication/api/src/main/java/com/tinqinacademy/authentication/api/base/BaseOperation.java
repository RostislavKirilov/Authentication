package com.tinqinacademy.authentication.api.base;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.convert.ConversionService;

@Getter
@Setter
public class BaseOperation {

    protected final Validator validator;
    protected final ConversionService conversionService;
    protected final ErrorMapper errorMapper;

    protected BaseOperation ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper ) {
        this.validator = validator;
        this.conversionService = conversionService;
        this.errorMapper = errorMapper;
    }
}
