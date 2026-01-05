package com.itau.transferencia.requests;

import com.itau.transferencia.validations.AccountNumber;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CustomerRequest(
        @NotBlank(message = "Name is required")
        String name,

        @AccountNumber
        String account,

        @Min(value = 0, message = "Balance must be positive")
        BigDecimal balance
) {
    public CustomerRequest {
        balance = balance == null ? BigDecimal.ZERO : balance;
    }
}
