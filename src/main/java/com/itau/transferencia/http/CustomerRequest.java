package com.itau.transferencia.http;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CustomerRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Account is required")
        @Size(min = 7, max = 7, message = "Account digits must be xxxxx-x")
        String account,

        BigDecimal balance
) {
    public CustomerRequest {
        balance = BigDecimal.ZERO;
    }
}
