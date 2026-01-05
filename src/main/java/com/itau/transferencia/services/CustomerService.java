package com.itau.transferencia.services;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.dtos.CustomerDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer create(CustomerDTO request);

    List<Customer> findAll();

    Optional<Customer> findByAccount(String account);
}
