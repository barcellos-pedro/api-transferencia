package com.itau.transferencia;

import com.itau.transferencia.services.CustomerService;
import com.itau.transferencia.services.TransferLogService;
import com.itau.transferencia.services.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TransferenciaApplicationTests {

    @Autowired
    CustomerService customerService;

    @Autowired
    TransferService transferService;

    @Autowired
    TransferLogService transferLogService;

    @Test
    void contextLoads() {
    }

    @Test
    void servicesInjection() {
        assertNotNull(customerService, "The CustomerService should be loaded into the context");
        assertNotNull(transferService, "The TransferService should be loaded into the context");
        assertNotNull(transferLogService, "The TransferLogService should be loaded into the context");
    }
}
