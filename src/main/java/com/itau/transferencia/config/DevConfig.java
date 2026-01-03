package com.itau.transferencia.config;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.repositories.CustomerRepository;
import com.itau.transferencia.repositories.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Profile("dev")
public class DevConfig {
    public static final Logger logger = LoggerFactory.getLogger(DevConfig.class);

    public static final List<Customer> CUSTOMERS = List.of(
            new Customer("Pedro", "00001-1", BigDecimal.valueOf(100)),
            new Customer("Bruno", "00002-2", BigDecimal.valueOf(900)),
            new Customer("Daniel", "00003-3", BigDecimal.valueOf(300))
    );

    public static final List<Transfer> TRANSFERS = List.of(
            new Transfer(CUSTOMERS.getFirst(), CUSTOMERS.getLast(), BigDecimal.valueOf(250.99)),
            new Transfer(CUSTOMERS.getFirst(), CUSTOMERS.get(1), BigDecimal.valueOf(100)),
            new Transfer(CUSTOMERS.get(1), CUSTOMERS.getLast(), BigDecimal.valueOf(30))
    );

    @Bean
    public CommandLineRunner initDatabase(CustomerRepository customerRepository, TransferRepository transferRepository) {
        return args -> {
            logger.info("Iniciando Banco de Dados");
            customerRepository.saveAll(CUSTOMERS);
            transferRepository.saveAll(TRANSFERS);
        };
    }
}
