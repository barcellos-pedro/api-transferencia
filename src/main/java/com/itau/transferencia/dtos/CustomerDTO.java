package com.itau.transferencia.dtos;

import com.itau.transferencia.validations.AccountNumber;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CustomerDTO(
        @NotBlank(message = "Name is required")
        String name,

        @AccountNumber
        String account,

        @Min(value = 0, message = "Balance must be positive")
        BigDecimal balance
) {
    public CustomerDTO {
        balance = balance == null ? BigDecimal.ZERO : balance;
    }
}
