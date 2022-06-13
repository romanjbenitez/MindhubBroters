package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.services.AccountsService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class AccountsController {


    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountsService accountsService;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountsService.getAccounts();
    }


    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {
        return accountsService.getAccount(id);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createNewAccount(Authentication authentication) {
        if (clientService.getCurrentClient(authentication).getAccounts().size() == 3) {
            return new ResponseEntity<Object>("you already have three accounts", HttpStatus.FORBIDDEN);
        }
        Set<String> numbersAccount = accountsService.getNumbersOfAccount();

        String randomNumber = "VIN" + getRandomNumber(10000000, 99999999);
        while (numbersAccount.contains(randomNumber)) {
            randomNumber = "VIN" + getRandomNumber(10000000, 99999999);
        }
        Account newAccount = new Account(randomNumber, LocalDateTime.now(), 0);

        clientService.getCurrentClient(authentication).addAccount(newAccount);
        accountsService.saveAccount(newAccount);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


}
