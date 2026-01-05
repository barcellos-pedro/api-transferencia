package com.itau.transferencia.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public record TransferDTO(
        Long id,
        String sourceAccount,
        String destinationAccount,
        BigDecimal amount,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt,
        TransferStatus status
) {
    public static TransferDTO fromEntity(Transfer transfer) {
        var destination = getAccount(transfer.getDestination());
        var source = getAccount(transfer.getSource());
        var createdAt = transfer.getCreatedAt();
        var amount = transfer.getAmount();
        var status = transfer.getStatus();
        var id = transfer.getId();
        return new TransferDTO(id, source, destination, amount, createdAt, status);
    }

    private static String getAccount(Customer customer) {
        return Optional.ofNullable(customer)
                .map(Customer::getAccount)
                .orElse("INVALID_ACCOUNT");
    }
}
