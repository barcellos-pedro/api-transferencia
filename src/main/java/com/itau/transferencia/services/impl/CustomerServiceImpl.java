package com.itau.transferencia.services.impl;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.http.requests.CustomerRequest;
import com.itau.transferencia.repositories.CustomerRepository;
import com.itau.transferencia.services.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer create(CustomerRequest request) {
        return repository.save(Customer.fromRequest(request));
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Customer> findByAccount(String account) {
        return repository.findByAccount(account);
    }
}