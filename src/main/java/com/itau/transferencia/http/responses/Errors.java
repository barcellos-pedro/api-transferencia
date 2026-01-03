package com.itau.transferencia.http.responses;

import java.util.List;

public record Errors(List<String> errors) {
    public static Errors of(String error) {
        return new Errors(List.of(error));
    }

    public static Errors of(List<String> errors) {
        return new Errors(errors);
    }

    public static Errors of(RuntimeException ex) {
        return new Errors(List.of(ex.getMessage()));
    }
}
