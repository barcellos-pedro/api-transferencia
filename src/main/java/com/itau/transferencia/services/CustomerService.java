package com.itau.transferencia.services;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.http.requests.CustomerRequest;
import com.itau.transferencia.http.requests.TransferRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer create(CustomerRequest request);

    List<Customer> findAll();

    Optional<Customer> findByAccount(String account);

    Transfer transfer(String account, TransferRequest transferRequest);

    List<Transfer> getTransfers(String account);
}
