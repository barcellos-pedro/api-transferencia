package com.itau.transferencia.http;

import com.itau.transferencia.entities.Customer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public class HttpHelper {
    private HttpHelper() {
    }

    public static URI getLocation(Customer customer) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(Map.of("id", customer.getId()))
                .toUri();
    }
}
