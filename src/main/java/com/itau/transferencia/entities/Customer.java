package com.itau.transferencia.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.itau.transferencia.dtos.request.CustomerDTO;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String account;

    private String name;

    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "source")
    @JsonManagedReference
    private List<Transfer> sentTransfers = new ArrayList<>();

    @OneToMany(mappedBy = "destination")
    @JsonManagedReference
    private List<Transfer> receivedTransfers = new ArrayList<>();

    @Version
    private Long version;

    public Customer() {
    }

    public Customer(String name, String account, BigDecimal balance) {
        this.name = name;
        this.account = account;
        this.balance = balance;
    }

    public Customer(String name, String account) {
        this.name = name;
        this.account = account;
        this.balance = BigDecimal.ZERO;
    }

    public Customer(CustomerDTO customerDTO) {
        this.name = customerDTO.name();
        this.account = customerDTO.account();
        this.balance = customerDTO.balance();
    }

    public static Customer fromRequest(CustomerDTO customerDTO) {
        return new Customer(customerDTO);
    }

    public static boolean isSameAccount(Customer customer, Customer other) {
        return customer.getAccount().equals(other.getAccount());
    }

    public boolean canTransfer(BigDecimal amount) {
        return this.getBalance().compareTo(amount) >= 0;
    }

    public boolean cannotTransfer(BigDecimal amount) {
        return !this.canTransfer(amount);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transfer> getSentTransfers() {
        return sentTransfers;
    }

    public void setSentTransfers(List<Transfer> sentTransfers) {
        this.sentTransfers = sentTransfers;
    }

    public List<Transfer> getReceivedTransfers() {
        return receivedTransfers;
    }

    public void setReceivedTransfers(List<Transfer> receivedTransfers) {
        this.receivedTransfers = receivedTransfers;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(getId(), customer.getId()) && Objects.equals(getAccount(), customer.getAccount()) && Objects.equals(getName(), customer.getName()) && Objects.equals(getBalance(), customer.getBalance());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAccount(), getName(), getBalance());
    }


    @Override
    public String toString() {
        return "Customer{id=%d, account='%s', name='%s', balance=%s,, version=%d}"
                .formatted(id, account, name, balance, version);
    }
}
