package com.github.oahnus.luqiancommon.config.valid;

import com.github.oahnus.luqiancommon.dto.RespData;
import com.github.oahnus.luqiancommon.enums.web.RespCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

/**
 * Created by oahnus on 2020-05-05
 * 16:36.
 */
@RestControllerAdvice
@ConditionalOnWebApplication
public class MyValidExceptinoHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public RespData constraintViolationException(ConstraintViolationException e) {
        return RespData.error(RespCode.PARAM_ERROR, e.getConstraintViolations().iterator().next().getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RespData methodArgsError(MethodArgumentNotValidException e) {
        ObjectError error = e.getBindingResult().getAllErrors().get(0);
        if (error instanceof FieldError) {
            String field = ((FieldError) error).getField();
            return RespData.error(RespCode.PARAM_ERROR, field + error.getDefaultMessage());
        }
        return RespData.error(RespCode.PARAM_ERROR, error.getDefaultMessage());
    }

    @ExceptionHandler(BindException.class)
    public RespData bindError(BindException e) {
        ObjectError error = e.getBindingResult().getAllErrors().get(0);
        return RespData.error(RespCode.PARAM_ERROR, error.getDefaultMessage());
    }
}
