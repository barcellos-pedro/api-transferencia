package com.itau.transferencia.dtos.response;

import java.util.List;

public record ErrorDTO(List<String> errors) {
    public static ErrorDTO of(String error) {
        return new ErrorDTO(List.of(error));
    }

    public static ErrorDTO of(List<String> errors) {
        return new ErrorDTO(errors);
    }
}
