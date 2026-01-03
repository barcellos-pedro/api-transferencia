package com.itau.transferencia.http.requests;

import com.itau.transferencia.validations.AccountNumber;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CustomerRequest(
        @NotBlank(message = "Name is required")
        String name,

        @AccountNumber
        String account,

        BigDecimal balance
) {
    public CustomerRequest {
        balance = BigDecimal.ZERO;
    }
}
