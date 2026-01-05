package com.itau.transferencia.exceptions;

import static com.itau.transferencia.helpers.ErrorMessages.SAME_ACCOUNT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class SameAccountException extends BusinessException {
    public SameAccountException() {
        super(SAME_ACCOUNT, BAD_REQUEST);
    }
}
