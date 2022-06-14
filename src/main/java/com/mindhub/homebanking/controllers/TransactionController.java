package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountsService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountsService accountsService;
    @Autowired
    private TransactionService transactionService;


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> newTransaction(@RequestParam Double ammount,
                                                 @RequestParam String description,
                                                 @RequestParam String originNumber,
                                                 @RequestParam String destinationNumber,
                                                 Authentication authentication) {
        Client currentClient = clientService.getCurrentClient(authentication);
        Account destinationAccount = accountsService.getAccountByNumber(destinationNumber);
        Account sourceAccount = accountsService.getAccountByNumber(originNumber);

        if (sourceAccount.getBalance() < ammount) {
            return new ResponseEntity<>("insufficient money ", HttpStatus.FORBIDDEN);
        }
        if (ammount.toString().isEmpty() || ammount <= 0 || description.isEmpty() || originNumber.isEmpty() || destinationNumber.isEmpty()) {
            if (ammount.toString().isEmpty()) {
                return new ResponseEntity<>("Missing amount", HttpStatus.FORBIDDEN);
            }
            if (ammount <= 0) {
                return new ResponseEntity<>("Amount cannot be negative", HttpStatus.FORBIDDEN);
            }
            if (description.isEmpty()) {
                return new ResponseEntity<>("Missing description", HttpStatus.FORBIDDEN);
            }
            if (originNumber.isEmpty()) {
                return new ResponseEntity<>("Missing origin number", HttpStatus.FORBIDDEN);
            }
            if (destinationNumber.isEmpty()) {
                return new ResponseEntity<>("Missing destination number", HttpStatus.FORBIDDEN);
            }

        }
        if (sourceAccount.equals(destinationAccount)) {
            return new ResponseEntity<>("The account number cannot be the same", HttpStatus.FORBIDDEN);
        }
        if (accountsService.getAccountByNumber(originNumber) == null) {
            return new ResponseEntity<>("Source account doesn't exist", HttpStatus.FORBIDDEN);
        }
        List<String> currentClientsAccountsNumbers = currentClient.getAccounts().stream().map(Account::getNumber).collect(Collectors.toList());

        if (!currentClientsAccountsNumbers.contains(originNumber)) {
            return new ResponseEntity<>("The source account doesn't belong to you", HttpStatus.FORBIDDEN);
        }
        if (accountsService.getAccountByNumber(destinationNumber) == null) {
            return new ResponseEntity<>("Destination account doesn't exist", HttpStatus.FORBIDDEN);
        }
        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, (-ammount), description + " " + destinationNumber, LocalDateTime.now());
        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, ammount, description + " " + originNumber, LocalDateTime.now());
        debitTransaction.setAccountBalance(sourceAccount.getBalance() - ammount);
        creditTransaction.setAccountBalance(destinationAccount.getBalance() + ammount);
        sourceAccount.setBalance(sourceAccount.getBalance() - ammount);
        destinationAccount.setBalance(destinationAccount.getBalance() + ammount);
        sourceAccount.addTransaction(debitTransaction);
        destinationAccount.addTransaction((creditTransaction));
        accountsService.saveAccount(sourceAccount);
        accountsService.saveAccount(destinationAccount);
        transactionService.saveTransaction(debitTransaction);
        transactionService.saveTransaction(creditTransaction);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }
}
