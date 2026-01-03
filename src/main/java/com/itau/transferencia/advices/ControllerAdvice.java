package com.itau.transferencia.advices;

import com.itau.transferencia.exceptions.BusinessException;
import com.itau.transferencia.http.helpers.ErrorResponse;
import com.itau.transferencia.http.responses.Errors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.itau.transferencia.advices.ControllerAdviceHelper.getErrorsMessage;
import static com.itau.transferencia.exceptions.ErrorMessages.ACCOUNT_EXISTS;
import static com.itau.transferencia.exceptions.ErrorMessages.BAD_JSON;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Errors> handleBusinessException(BusinessException exception) {
        return ErrorResponse.badRequest(exception);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Errors> handleDataIntegrityException(DataIntegrityViolationException exception) {
        return ErrorResponse.conflict(ACCOUNT_EXISTS);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleValidationException(MethodArgumentNotValidException exception) {
        var errors = getErrorsMessage(exception);
        return ErrorResponse.badRequest(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Errors> handleRequestBodyException(HttpMessageNotReadableException exception) {
        return ErrorResponse.badRequest(BAD_JSON);
    }
}
