package com.itau.transferencia.exceptions;

import static com.itau.transferencia.helpers.ErrorMessages.INSUFFICIENT_FUNDS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InsufficientFundsException extends BusinessException {
    public InsufficientFundsException() {
        super(INSUFFICIENT_FUNDS, BAD_REQUEST);
    }
}
