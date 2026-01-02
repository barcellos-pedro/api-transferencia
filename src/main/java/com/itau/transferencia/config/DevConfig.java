package com.itau.transferencia.config;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Profile("dev")
public class DevConfig {
    public static final List<Customer> CUSTOMERS = List.of(
            new Customer("Pedro", "00001-1", BigDecimal.valueOf(500)),
            new Customer("Bruno", "00002-2", BigDecimal.valueOf(700)),
            new Customer("Daniel", "00003-3", BigDecimal.valueOf(1000))
    );

    @Bean
    public CommandLineRunner initDatabase(CustomerRepository repository) {
        return args -> repository.saveAll(CUSTOMERS);
    }
}
