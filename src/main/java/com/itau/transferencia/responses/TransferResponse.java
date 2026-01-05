package com.itau.transferencia.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public record TransferResponse(
        Long id,
        String sourceAccount,
        String destinationAccount,
        BigDecimal amount,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt,
        TransferStatus status
) {
    public static TransferResponse fromEntity(Transfer transfer) {
        var destination = getAccount(transfer.getDestination());
        var source = getAccount(transfer.getSource());
        var createdAt = transfer.getCreatedAt();
        var amount = transfer.getAmount();
        var status = transfer.getStatus();
        var id = transfer.getId();
        return new TransferResponse(id, source, destination, amount, createdAt, status);
    }

    private static String getAccount(Customer customer) {
        return Optional.ofNullable(customer)
                .map(Customer::getAccount)
                .orElse("INVALID_ACCOUNT");
    }
}
