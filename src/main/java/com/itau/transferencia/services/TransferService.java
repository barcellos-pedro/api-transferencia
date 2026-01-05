package com.itau.transferencia.services;

import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.dtos.TransferDTO;
import com.itau.transferencia.responses.TransferResponse;

import java.util.List;

public interface TransferService {
    Transfer transfer(String account, TransferDTO transferDTO);

    List<TransferResponse> getTransfers(String account);
}
