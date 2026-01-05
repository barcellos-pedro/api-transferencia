package com.itau.transferencia.helpers;

public class ErrorMessages {
    public static final String ACCOUNT_NOT_FOUND = "Conta %s não encontrada.";
    public static final String UNIQUE_ACCOUNT = "Número da conta já existe.";
    public static final String SAME_ACCOUNT = "Não é possível transferir para mesma conta.";
    public static final String INSUFFICIENT_FUNDS = "Saldo insuficiente para esta operação.";
    public static final String ACCOUNT_NUMBER = "Account must be 5 digits, a hyphen, and 1 digit (7 total)";
    public static final String OPTIMISTIC_LOCK = "Conta atualizada por outro processo. Por favor, tente novamente.";
}
