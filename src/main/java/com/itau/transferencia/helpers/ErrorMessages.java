package com.itau.transferencia.helpers;

public class ErrorMessages {
    public static final String ACCOUNT_NOT_FOUND = "Account %s not found.";
    public static final String UNIQUE_ACCOUNT = "Unique account number already exists.";
    public static final String SAME_ACCOUNT = "Cannot transfer to the same account.";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds for this operation.";
    public static final String OPTIMISTIC_LOCK = "The account was updated by another process. Please try again.";
}
