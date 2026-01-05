package com.itau.transferencia.exceptions;

import static com.itau.transferencia.helpers.ErrorMessages.ACCOUNT_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String account) {
        super(ACCOUNT_NOT_FOUND.formatted(account), NOT_FOUND);
    }
}
