package com.itau.transferencia.dtos.request;

import com.itau.transferencia.validations.AccountNumber;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferDTO(
        @AccountNumber
        String destination,

        @NotNull(message = "Amount is required")
        @Max(value = 10_000, message = "Amount must not exceed 10,000")
        @Min(value = 1, message = "Amount must be at least greater than 1,00")
        BigDecimal amount
) {
}
