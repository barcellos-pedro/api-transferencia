package com.itau.transferencia.entities;

public enum TransferStatus {
    COMPLETED("Completed"),
    PENDING("Pending"),
    FAILED("Failed");

    private String status;

    TransferStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
