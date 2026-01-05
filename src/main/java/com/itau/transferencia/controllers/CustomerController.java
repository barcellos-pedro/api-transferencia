package com.itau.transferencia.controllers;

import com.itau.transferencia.dtos.request.CustomerDTO;
import com.itau.transferencia.dtos.response.TransferDTO;
import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.services.CustomerService;
import com.itau.transferencia.services.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final TransferService transferService;

    public CustomerController(CustomerService customerService, TransferService transferService) {
        this.customerService = customerService;
        this.transferService = transferService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        var customers = customerService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }

    @GetMapping("/search")
    public ResponseEntity<Customer> findByAccountNumber(@RequestParam String account) {
        return customerService.findByAccount(account)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody CustomerDTO request) {
        var newCustomer = customerService.create(request);
        var location = getLocation(newCustomer.getId());
        return ResponseEntity.created(location).body(newCustomer);
    }

    @GetMapping("/{account}/transfers")
    public ResponseEntity<?> getTransfers(@PathVariable String account) {
        var transfers = transferService.getTransfers(account);
        return ResponseEntity.ok(transfers);
    }

    @PostMapping("/{account}/transfers")
    public ResponseEntity<TransferDTO> transfer(
            @PathVariable String account,
            @Valid @RequestBody com.itau.transferencia.dtos.request.TransferDTO transferDTO
    ) {
        var transfer = transferService.transfer(account, transferDTO);
        var response = TransferDTO.fromEntity(transfer);
        return ResponseEntity.status(CREATED).body(response);
    }

    public static URI getLocation(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(Map.of("id", id))
                .toUri();
    }
}
