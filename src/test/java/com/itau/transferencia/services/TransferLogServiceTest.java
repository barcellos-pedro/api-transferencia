package com.itau.transferencia.services;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.repositories.TransferRepository;
import com.itau.transferencia.services.impl.TransferLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferLogServiceTest {

    @InjectMocks
    private TransferLogServiceImpl service;

    @Mock
    private TransferRepository repository;

    @Test
    void save() {
        var source = new Customer("Bob", "00005-5");
        var destination = new Customer("Ross", "00006-6", BigDecimal.valueOf(50));
        var transfer = Transfer.ofCompleted(source, destination, BigDecimal.valueOf(50));

        service.save(transfer);

        verify(repository, times(1)).save(transfer);
    }
}