package com.itau.transferencia.advices;

import com.itau.transferencia.exceptions.BusinessException;
import com.itau.transferencia.responses.Errors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.itau.transferencia.helpers.ErrorMessages.OPTIMISTIC_LOCK;
import static com.itau.transferencia.helpers.ErrorMessages.UNIQUE_ACCOUNT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Errors> handleBusinessException(BusinessException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(Errors.of(exception.getMessage()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Errors> handleOptimisticLockingException(ObjectOptimisticLockingFailureException exception) {
        return ResponseEntity.badRequest().body(Errors.of(OPTIMISTIC_LOCK));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Errors> handleDataIntegrityException(DataIntegrityViolationException exception) {
        return ResponseEntity.status(CONFLICT).body(Errors.of(UNIQUE_ACCOUNT));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleValidationException(MethodArgumentNotValidException exception) {
        var errors = getErrorsMessage(exception);
        return ResponseEntity.badRequest().body(Errors.of(errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Errors> handleRequestBodyException(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(Errors.of(BAD_REQUEST.getReasonPhrase()));
    }

    public static List<String> getErrorsMessage(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
    }
}
