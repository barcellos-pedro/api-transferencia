package com.itau.transferencia.http;

import java.util.List;

public record ErrorResponse(List<String> errors) {
    public static ErrorResponse of(String error) {
        return new ErrorResponse(List.of(error));
    }

    public static ErrorResponse of(List<String> errors) {
        return new ErrorResponse(errors);
    }
}
