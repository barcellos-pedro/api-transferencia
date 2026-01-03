package com.itau.transferencia.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "source_id")
    private Customer source;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "destination_id")
    private Customer destination;

    private BigDecimal amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    public Transfer() {
    }

    public Transfer(Customer source, Customer destination, BigDecimal amount, TransferStatus status) {
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
        this.status = status;
    }

    public static Transfer ofCompleted(Customer source, Customer destination, BigDecimal amount) {
        return new Transfer(source, destination, amount, TransferStatus.COMPLETED);
    }

    public static Transfer ofFailed(Customer source, Customer destination, BigDecimal amount) {
        return new Transfer(source, destination, amount, TransferStatus.FAILED);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getSource() {
        return source;
    }

    public void setSource(Customer source) {
        this.source = source;
    }

    public Customer getDestination() {
        return destination;
    }

    public void setDestination(Customer destination) {
        this.destination = destination;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transfer transfer)) return false;
        return Objects.equals(getId(), transfer.getId()) && Objects.equals(getSource(), transfer.getSource()) && Objects.equals(getDestination(), transfer.getDestination()) && Objects.equals(getAmount(), transfer.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSource(), getDestination(), getAmount());
    }

    @Override
    public String toString() {
        return "Transfer{id=%d, source='%s', destination='%s', amount=%s, createdAt=%s, status=%s}"
                .formatted(id, source, destination, amount, createdAt, status);
    }
}
