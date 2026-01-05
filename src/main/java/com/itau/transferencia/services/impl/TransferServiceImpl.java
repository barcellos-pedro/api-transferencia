package com.itau.transferencia.services.impl;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.exceptions.BusinessException;
import com.itau.transferencia.http.requests.TransferRequest;
import com.itau.transferencia.http.responses.TransferResponse;
import com.itau.transferencia.repositories.TransferRepository;
import com.itau.transferencia.services.CustomerService;
import com.itau.transferencia.services.TransferLogService;
import com.itau.transferencia.services.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.itau.transferencia.exceptions.ErrorMessages.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TransferServiceImpl implements TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);
    private final TransferRepository repository;
    private final CustomerService customerService;
    private final TransferLogService transferLogService;

    public TransferServiceImpl(
            TransferRepository repository,
            CustomerService customerService,
            TransferLogService transferLogService
    ) {
        this.repository = repository;
        this.customerService = customerService;
        this.transferLogService = transferLogService;
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
                throw new BusinessException(SAME_ACCOUNT, BAD_REQUEST);
            }

            if (source.cannotTransfer(transferRequest.amount())) {
                throw new BusinessException(INSUFFICIENT_FUNDS, BAD_REQUEST);
            }

            source.setBalance(source.getBalance().subtract(amount));
            destination.setBalance(destination.getBalance().add(amount));

            return repository.save(Transfer.ofCompleted(source, destination, amount));
        } catch (ObjectOptimisticLockingFailureException | BusinessException exception) {
            logger.warn(exception.getMessage(), exception);
            transferLogService.save(Transfer.ofFailed(source, destination, transferRequest.amount()));
            throw exception;
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
                .orElseThrow(() -> new BusinessException(ACCOUNT_NOT_FOUND.formatted(account), NOT_FOUND));
    }
}
