package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientPayDTO;
import com.mindhub.homebanking.dtos.PayDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountsService;
import com.mindhub.homebanking.services.CardsService;
import com.mindhub.homebanking.services.PayService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PayController {
    @Autowired
    PayService payService;
    @Autowired
    CardsService cardsService;
    @Autowired
    AccountsService accountsService;
    @Autowired
    TransactionService transactionService;


    @Transactional
    @PostMapping("pay")
    public ResponseEntity<Object> newPay(@RequestBody ClientPayDTO clientPayDTO) {
        if (clientPayDTO.getAmount().isNaN() || clientPayDTO.getAmount() <= 0) {
            return new ResponseEntity<Object>("Please insert a valid amount", HttpStatus.FORBIDDEN);
        }
        if (clientPayDTO.getNumber().isEmpty()) {
            return new ResponseEntity<Object>("Please insert a number", HttpStatus.FORBIDDEN);
        }
        if (clientPayDTO.getCvv() < 100 || clientPayDTO.getCvv() > 999) {
            return new ResponseEntity<Object>("Please insert a valid cvv", HttpStatus.FORBIDDEN);
        }
        if (clientPayDTO.getDescription().isEmpty()) {
            return new ResponseEntity<Object>("Please insert a description", HttpStatus.FORBIDDEN);
        }
        Card currentCard = cardsService.getCardByNumber(clientPayDTO.getNumber());
        if (currentCard == null) {
            return new ResponseEntity<Object>("Invalid card, please verify the number", HttpStatus.FORBIDDEN);
        }
        if (currentCard.getCvv() != clientPayDTO.getCvv()) {
            return new ResponseEntity<Object>("Invalid card, please check the cvv", HttpStatus.FORBIDDEN);
        }
        if (!currentCard.getThruDate().isAfter(LocalDateTime.now())) {
            return new ResponseEntity<Object>("The card is expired ", HttpStatus.FORBIDDEN);
        }
        Client cardClient = currentCard.getClient();
        Account accountToDebit = cardClient.getAccounts().stream().filter(account -> account.getBalance() > clientPayDTO.getAmount()).findFirst().orElse(null);
        if (accountToDebit == null) {
            return new ResponseEntity<Object>("You don't have enough funds", HttpStatus.FORBIDDEN);
        }
        Pay newPay = new Pay(clientPayDTO.getNumber(), clientPayDTO.getCvv(), clientPayDTO.getAmount(), clientPayDTO.getDescription());
        Transaction newTransaction = new Transaction(TransactionType.DEBIT, -clientPayDTO.getAmount(), clientPayDTO.getDescription(),LocalDateTime.now());
        accountToDebit.setBalance(accountToDebit.getBalance() - clientPayDTO.getAmount());
        accountToDebit.addTransaction(newTransaction);
        payService.savePays(newPay);
        transactionService.saveTransaction(newTransaction);
        accountsService.saveAccount(accountToDebit);

        return new ResponseEntity<Object>("Pay accepted", HttpStatus.ACCEPTED);
    }

    @GetMapping("pays")
    public List<PayDTO> getPays() {
        return payService.getPays();
    }

}
