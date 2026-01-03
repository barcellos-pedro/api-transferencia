package com.itau.transferencia.controllers;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.http.helpers.HttpHelper;
import com.itau.transferencia.http.requests.CustomerRequest;
import com.itau.transferencia.http.requests.TransferRequest;
import com.itau.transferencia.http.responses.TransferResponse;
import com.itau.transferencia.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        var customers = service.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }

    @GetMapping("/search")
    public ResponseEntity<Customer> findByAccountNumber(@RequestParam String account) {
        return service.findByAccount(account)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody CustomerRequest request) {
        var newCustomer = service.create(request);
        var location = HttpHelper.getLocation(newCustomer.getId());
        return ResponseEntity.created(location).body(newCustomer);
    }

    @GetMapping("/{account}/transfers")
    public ResponseEntity<?> getTransfers(@PathVariable String account) {
        var transfers = service.getTransfers(account);
        return ResponseEntity.ok(transfers);
    }

    @PostMapping("/{account}/transfers")
    public ResponseEntity<TransferResponse> transfer(@PathVariable String account, @Valid @RequestBody TransferRequest transferRequest) {
        var transfer = service.transfer(account, transferRequest);
        var response = TransferResponse.fromEntity(transfer);
        return ResponseEntity.status(CREATED).body(response);
    }
}
