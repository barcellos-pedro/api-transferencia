package com.itau.transferencia.responses;

import java.util.List;

public record Errors(List<String> errors) {
    public static Errors of(String error) {
        return new Errors(List.of(error));
    }

    public static Errors of(List<String> errors) {
        return new Errors(errors);
    }
}
