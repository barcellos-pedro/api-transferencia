package com.itau.transferencia.advices;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

public class ControllerAdviceHelper {
    public static List<String> getErrorsMessage(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList();
    }
}
