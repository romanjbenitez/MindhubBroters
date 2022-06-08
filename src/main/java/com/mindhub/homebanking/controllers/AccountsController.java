package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountsController {
    @Autowired
    private AccountRepository repository;

    @Autowired
    private ClientRepository repo;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return repository.findAll().stream().map(AccountDTO::new).collect(toList());}


    @GetMapping("/accounts/{id}")
    public AccountDTO getClient(@PathVariable Long id){
        return repository.findById(id).map(AccountDTO::new).orElse(null);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createNewAccount(Authentication authentication){
            if(repo.findByEmail(authentication.getName()).getAccounts().size() == 3){
                return new ResponseEntity<Object>("you already have three accounts", HttpStatus.FORBIDDEN);
            }
            Set<String> numbersAccount = repository.findAll().stream().map(Account::getNumber).collect(Collectors.toSet());
            String randomNumber = "VIN" + getRandomNumber(10000000, 99999999);
            while(numbersAccount.contains(randomNumber)){
                randomNumber = "VIN" + getRandomNumber(10000000, 99999999);
            }
            Account newAccount = new Account( randomNumber, LocalDateTime.now(), 0);

            repo.findByEmail(authentication.getName()).addAccount(newAccount);
            repository.save(newAccount);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public int getRandomNumber(int min, int max) {
        return (int)((Math.random() * (max - min)) + min);
    }



}
