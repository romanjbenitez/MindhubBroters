package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TransactionService {
    void saveTransaction(Transaction transaction);

    List<Transaction> getTransactionsBetweenDates(LocalDateTime from, LocalDateTime to);
}
