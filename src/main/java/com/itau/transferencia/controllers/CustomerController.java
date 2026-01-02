package com.itau.transferencia.controllers;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.http.HttpHelper;
import com.itau.transferencia.repositories.CustomerRepository;
import com.itau.transferencia.http.CustomerRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository customerRepository) {
        this.repository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        var customers = repository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }

    @GetMapping("/search")
    public ResponseEntity<Customer> findByAccount(@RequestParam String account) {
        return repository.findByAccount(account)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody CustomerRequest request) {
        var newCustomer = repository.save(Customer.fromRequest(request));
        var location = HttpHelper.getLocation(newCustomer);
        return ResponseEntity.created(location).body(newCustomer);
    }
}
