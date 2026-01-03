package com.itau.transferencia.services.impl;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.entities.TransferStatus;
import com.itau.transferencia.exceptions.BusinessException;
import com.itau.transferencia.exceptions.ErrorMessages;
import com.itau.transferencia.http.requests.CustomerRequest;
import com.itau.transferencia.http.requests.TransferRequest;
import com.itau.transferencia.repositories.CustomerRepository;
import com.itau.transferencia.repositories.TransferRepository;
import com.itau.transferencia.services.CustomerService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository repository;
    private final TransferRepository transferRepository;

    public CustomerServiceImpl(CustomerRepository repository, TransferRepository transferRepository) {
        this.repository = repository;
        this.transferRepository = transferRepository;
    }

    @Override
    public Customer create(@Valid @RequestBody CustomerRequest request) {
        return repository.save(Customer.fromRequest(request));
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Customer> findByAccount(@RequestParam String account) {
        return repository.findByAccount(account);
    }

    @Override
    @Transactional
    public Transfer transfer(String source, TransferRequest transferRequest) {
        var amount = transferRequest.amount();
        var sourceAccount = findAccount(source);
        var destinationAccount = findAccount(transferRequest.destination());

        try {
            if (sourceAccount.isSameAccount(destinationAccount)) {
                throw new BusinessException("Cannot transfer to the same account.");
            }

            if (insufficientFunds(transferRequest, sourceAccount)) {
                throw new BusinessException("Insufficient funds for this operation.");
            }

            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
            destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

            return transferRepository.save(new Transfer(sourceAccount, destinationAccount, amount, TransferStatus.COMPLETED));
        } catch (ObjectOptimisticLockingFailureException exception) {
            logger.error(ErrorMessages.OPTIMISTIC_LOCK);
            return transferRepository.save(new Transfer(sourceAccount, destinationAccount, BigDecimal.ZERO, TransferStatus.FAILED));
        } catch (BusinessException exception) {
            logger.error(exception.getMessage());
            return transferRepository.save(new Transfer(sourceAccount, destinationAccount, BigDecimal.ZERO, TransferStatus.FAILED));
        }
    }

    @Override
    public List<Transfer> getTransfers(String account) {
        return transferRepository.getTransfers(account);
    }

    private Customer findAccount(String account) {
        return repository.findByAccount(account)
                .orElseThrow(() -> new BusinessException("Account " + account + " not found"));
    }

    private static boolean insufficientFunds(TransferRequest transferRequest, Customer from) {
        return from.getBalance().compareTo(transferRequest.amount()) < 0;
    }
}