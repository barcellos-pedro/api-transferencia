package com.itau.transferencia.services.impl;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.exceptions.AccountNotFoundException;
import com.itau.transferencia.exceptions.BusinessException;
import com.itau.transferencia.exceptions.InsufficientFundsException;
import com.itau.transferencia.exceptions.SameAccountException;
import com.itau.transferencia.repositories.TransferRepository;
import com.itau.transferencia.dtos.TransferDTO;
import com.itau.transferencia.responses.TransferResponse;
import com.itau.transferencia.services.CustomerService;
import com.itau.transferencia.services.TransferLogService;
import com.itau.transferencia.services.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Transfer transfer(String sourceAccount, TransferDTO transferDTO) {
        Customer source = null;
        Customer destination = null;

        try {
            var amount = transferDTO.amount();
            source = findAccount(sourceAccount);
            destination = findAccount(transferDTO.destination());

            if (Customer.isSameAccount(source, destination)) {
                throw new SameAccountException();
            }

            if (source.cannotTransfer(transferDTO.amount())) {
                throw new InsufficientFundsException();
            }

            source.setBalance(source.getBalance().subtract(amount));
            destination.setBalance(destination.getBalance().add(amount));

            return repository.save(Transfer.ofCompleted(source, destination, amount));
        } catch (BusinessException exception) {
            var failedTransfer = Transfer.ofFailed(source, destination, transferDTO.amount());
            logger.warn(failedTransfer.toString(), exception);
            transferLogService.save(failedTransfer);
            throw exception;
        } catch (ObjectOptimisticLockingFailureException exception) {
            var transfer = Transfer.ofFailed(source, destination, transferDTO.amount());
            logger.error(transfer.toString(), exception);
            transferLogService.save(transfer);
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
                .orElseThrow(() -> new AccountNotFoundException(account));
    }
}
