package com.itau.transferencia.services;

import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.dtos.response.TransferDTO;

import java.util.List;

public interface TransferService {
    Transfer transfer(String account, com.itau.transferencia.dtos.request.TransferDTO transferDTO);

    List<TransferDTO> getTransfers(String account);
}
