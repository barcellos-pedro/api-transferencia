package com.itau.transferencia.repositories;

import com.itau.transferencia.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT t FROM transfers t WHERE t.source.account = :account OR t.destination.account = :account ORDER BY t.createdAt DESC")
    List<Transfer> getTransfers(@Param("account") String account);
}
