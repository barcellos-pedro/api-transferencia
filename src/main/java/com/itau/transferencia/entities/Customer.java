package com.itau.transferencia.entities;

import com.itau.transferencia.http.CustomerRequest;
import jakarta.persistence.*;

import java.math.BigDecimal;
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

    public Customer() {
    }

    public Customer(String name, String account, BigDecimal balance) {
        this.name = name;
        this.account = account;
        this.balance = balance;
    }

    public Customer(CustomerRequest dto) {
        this.name = dto.name();
        this.account = dto.account();
        this.balance = dto.balance();
    }

    public static Customer fromRequest(CustomerRequest dto) {
        return new Customer(dto);
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
        return "Customer{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
