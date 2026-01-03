package com.itau.transferencia.http.helpers;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public class HttpHelper {
    private HttpHelper() {
    }

    public static URI getLocation(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(Map.of("id", id))
                .toUri();
    }
}
