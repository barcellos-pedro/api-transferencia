package com.itau.transferencia.http.helpers;

import com.itau.transferencia.exceptions.BusinessException;
import com.itau.transferencia.http.responses.Errors;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;

public class ErrorResponse {
    private ErrorResponse() {
    }

    public static ResponseEntity<Errors> fromBusinessException(BusinessException exception) {
        var status = exception.getHttpStatus();
        var body = Errors.of(exception);
        return ResponseEntity.status(status).body(body);
    }

    public static ResponseEntity<Errors> badRequest(String message) {
        return ResponseEntity.badRequest().body(Errors.of(message));
    }

    public static ResponseEntity<Errors> badRequest(List<String> message) {
        return ResponseEntity.badRequest().body(Errors.of(message));
    }

    public static ResponseEntity<Errors> conflict(String message) {
        return ResponseEntity.status(CONFLICT).body(Errors.of(message));
    }
}
