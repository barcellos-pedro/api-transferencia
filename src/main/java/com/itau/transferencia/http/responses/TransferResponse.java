package com.itau.transferencia.http.responses;

import com.itau.transferencia.entities.Transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
        Long id,
        String sourceAccount,
        String destinationAccount,
        BigDecimal amount,
        LocalDateTime createdAt
) {
    public static TransferResponse fromEntity(Transfer transfer) {
        var source = transfer.getSource().getAccount();
        var destination = transfer.getDestination().getAccount();
        var id = transfer.getId();
        var amount = transfer.getAmount();
        var dateTime = transfer.getCreatedAt();
        return new TransferResponse(id, source, destination, amount, dateTime);
    }
}
