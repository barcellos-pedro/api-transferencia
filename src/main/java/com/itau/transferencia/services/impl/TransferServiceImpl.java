package com.itau.transferencia.services.impl;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.exceptions.BusinessException;
import com.itau.transferencia.http.requests.TransferRequest;
import com.itau.transferencia.http.responses.TransferResponse;
import com.itau.transferencia.repositories.TransferRepository;
import com.itau.transferencia.services.CustomerService;
import com.itau.transferencia.services.TransferService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);
    private final TransferRepository repository;
    private final CustomerService customerService;

    public TransferServiceImpl(TransferRepository repository, CustomerService customerService) {
        this.repository = repository;
        this.customerService = customerService;
    }

    @Override
    @Transactional
    public Transfer transfer(String sourceAccount, TransferRequest transferRequest) {
        Customer source = null;
        Customer destination = null;

        try {
            var amount = transferRequest.amount();
            source = findAccount(sourceAccount);
            destination = findAccount(transferRequest.destination());

            if (Customer.isSameAccount(source, destination)) {
                throw new BusinessException("Cannot transfer to the same account.");
            }

            if (!source.hasFundsToTransfer(transferRequest.amount())) {
                throw new BusinessException("Insufficient funds for this operation.");
            }

            source.setBalance(source.getBalance().subtract(amount));
            destination.setBalance(destination.getBalance().add(amount));

            return repository.save(Transfer.ofCompleted(source, destination, amount));
        } catch (ObjectOptimisticLockingFailureException | BusinessException exception) {
            var transfer = Transfer.ofFailed(source, destination, transferRequest.amount());
            logger.error(exception.getMessage(), exception);
            return repository.save(transfer);
        }
    }

    @Override
    public List<TransferResponse> getTransfers(String account) {
        return repository.getTransfers(account)
                .stream()
                .map(TransferResponse::fromEntity)
                .toList();
    }

    private Customer findAccount(String account) {
        return customerService.findByAccount(account)
                .orElseThrow(() -> new BusinessException("Account " + account + " not found"));
    }
}
