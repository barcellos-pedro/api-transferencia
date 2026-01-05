package com.itau.transferencia.enums;

public enum TransferStatus {
    COMPLETED("Completed"),
    PENDING("Pending"),
    FAILED("Failed");

    private final String status;

    TransferStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
