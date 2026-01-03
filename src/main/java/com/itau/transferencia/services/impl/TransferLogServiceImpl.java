package com.itau.transferencia.services.impl;

import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.repositories.TransferRepository;
import com.itau.transferencia.services.TransferLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferLogServiceImpl implements TransferLogService {
    private final TransferRepository repository;

    public TransferLogServiceImpl(TransferRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Transfer transfer) {
        repository.save(transfer);
    }
}
