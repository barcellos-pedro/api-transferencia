package com.itau.transferencia.exceptions;

import org.springframework.http.HttpStatus;

public class UniqueAccountException extends BusinessException {
    public UniqueAccountException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
