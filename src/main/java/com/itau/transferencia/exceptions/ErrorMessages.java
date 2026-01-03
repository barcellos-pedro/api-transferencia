package com.itau.transferencia.exceptions;

public class ErrorMessages {
    private ErrorMessages() {
    }

    public static final String ACCOUNT_EXISTS = "Account number already exists.";
    public static final String BAD_JSON = "Malformed JSON request or missing request body";
    public static final String OPTIMISTIC_LOCK = "The account was updated by another process. Please try again.";
}
