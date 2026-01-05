package com.itau.transferencia.services;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.requests.CustomerRequest;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer create(CustomerRequest request);

    List<Customer> findAll();

    Optional<Customer> findByAccount(String account);
}
