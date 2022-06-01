package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> newTransaction(@RequestParam Double ammount,
                                                 @RequestParam String description,
                                                 @RequestParam String originNumber,
                                                 @RequestParam String destinationNumber,
                                                 Authentication authentication){
        Client currentClient = clientRepository.findByEmail(authentication.getName());
        Account destinationAccount =  accountRepository.findByNumber(destinationNumber);
        Account sourceAccount =  accountRepository.findByNumber(originNumber);

        if(sourceAccount.getBalance() < ammount){
            return  new ResponseEntity<Object>("insufficient money ", HttpStatus.FORBIDDEN);
        }


        if (ammount.toString().isEmpty() || description.isEmpty() || originNumber.isEmpty() || destinationNumber.isEmpty()) {
            return new ResponseEntity<Object>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (sourceAccount.equals(destinationAccount)){
            return  new ResponseEntity<Object>("The account number cannot be the same", HttpStatus.FORBIDDEN);
        }
        if(accountRepository.findByNumber(originNumber) == null) {
            return  new ResponseEntity<Object>("Source account doesn't exist", HttpStatus.FORBIDDEN);
        }
        List<String> currentClientsAccountsNumbers = currentClient.getAccounts().stream().map(account -> account.getNumber()).collect(Collectors.toList());
        if(!currentClientsAccountsNumbers.contains(originNumber)){
            return  new ResponseEntity<Object>("The source account doesn't belong to you", HttpStatus.FORBIDDEN);
        }
        if(accountRepository.findByNumber(destinationNumber) == null) {
            return  new ResponseEntity<Object>("Destination account doesn't exist", HttpStatus.FORBIDDEN);
        }

        sourceAccount.setBalance(sourceAccount.getBalance() - ammount);
        destinationAccount.setBalance(sourceAccount.getBalance() + ammount);
        Transaction debitTransaction = new Transaction(TransactionType.debito, (-ammount), description +" " +destinationNumber , LocalDateTime.now());
        Transaction creditTransaction = new Transaction(TransactionType.credito, ammount,description + " " + originNumber, LocalDateTime.now());
        sourceAccount.addTransaction(debitTransaction);
        destinationAccount.addTransaction((creditTransaction));
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        return  new ResponseEntity<Object>("Success", HttpStatus.CREATED);
    }




}
