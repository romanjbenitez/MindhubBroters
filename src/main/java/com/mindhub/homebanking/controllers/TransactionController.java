package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> newTransaction(@RequestParam Double ammount,
                                                 @RequestParam String description,
                                                 @RequestParam String originNumber,
                                                 @RequestParam String destinationNumber,
                                                 Authentication authentication) {
        Client currentClient = clientRepository.findByEmail(authentication.getName());
        Account destinationAccount = accountRepository.findByNumber(destinationNumber);
        Account sourceAccount = accountRepository.findByNumber(originNumber);

        if (sourceAccount.getBalance() < ammount) {
            return new ResponseEntity<>("insufficient money ", HttpStatus.FORBIDDEN);
        }
        if (ammount.toString().isEmpty()|| ammount < 0 || description.isEmpty() || originNumber.isEmpty() || destinationNumber.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (sourceAccount.equals(destinationAccount)) {
            return new ResponseEntity<>("The account number cannot be the same", HttpStatus.FORBIDDEN);
        }
        if (accountRepository.findByNumber(originNumber) == null) {
            return new ResponseEntity<>("Source account doesn't exist", HttpStatus.FORBIDDEN);
        }
        List<String> currentClientsAccountsNumbers = currentClient.getAccounts().stream().map(Account::getNumber).collect(Collectors.toList());

        if (!currentClientsAccountsNumbers.contains(originNumber)) {
            return new ResponseEntity<>("The source account doesn't belong to you", HttpStatus.FORBIDDEN);
        }
        if (accountRepository.findByNumber(destinationNumber) == null) {
            return new ResponseEntity<>("Destination account doesn't exist", HttpStatus.FORBIDDEN);
        }

        sourceAccount.setBalance(sourceAccount.getBalance() - ammount);
        destinationAccount.setBalance(destinationAccount.getBalance() + ammount);
        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, (-ammount), description + " " + destinationNumber, LocalDateTime.now());
        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, ammount, description + " " + originNumber, LocalDateTime.now());
        sourceAccount.addTransaction(debitTransaction);
        destinationAccount.addTransaction((creditTransaction));
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        transactionRepository.save(debitTransaction);


        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }
}
