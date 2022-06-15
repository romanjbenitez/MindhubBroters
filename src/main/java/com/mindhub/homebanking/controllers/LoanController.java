package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.ClientLoanRepository;
import com.mindhub.homebanking.services.AccountsService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.LoanService;
import com.mindhub.homebanking.services.TransactionService;
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
public class LoanController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountsService accountsService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ClientLoanRepository clientLoanRepository;

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> addLoan(@RequestBody LoanApplicationDTO LoanApplicationDTO, Authentication authentication) {
        if (LoanApplicationDTO.getId() <= 0 || LoanApplicationDTO.getPayments() <= 0 || LoanApplicationDTO.getAccountNumber().isEmpty() || LoanApplicationDTO.getAmmount().toString().isEmpty() || LoanApplicationDTO.getAmmount() <= 0) {
            if (LoanApplicationDTO.getId() <= 0) {
                return new ResponseEntity<>("Invalid Loan", HttpStatus.FORBIDDEN);
            }
            if (LoanApplicationDTO.getPayments() <= 0) {
                return new ResponseEntity<>("Missing payments", HttpStatus.FORBIDDEN);
            }
            if (LoanApplicationDTO.getAccountNumber().isEmpty()) {
                return new ResponseEntity<>("Missing account number", HttpStatus.FORBIDDEN);
            }
            if (LoanApplicationDTO.getAmmount().toString().isEmpty()) {
                return new ResponseEntity<>("Missing amount", HttpStatus.FORBIDDEN);
            }
            if (LoanApplicationDTO.getAmmount() <= 0) {
                return new ResponseEntity<>("Invalid amount", HttpStatus.FORBIDDEN);
            }
        }


        Loan loan = loanService.getLoanById(LoanApplicationDTO.getId());
        Account destinyAccount = accountsService.getAccountByNumber(LoanApplicationDTO.getAccountNumber());
        Client clientAuth = clientService.getCurrentClient(authentication);
        if (clientAuth == null) {
            return new ResponseEntity<>("rejected the account muts be your", HttpStatus.FORBIDDEN);
        }
        if (loan == null) {
            return new ResponseEntity<>("Invalid Loan", HttpStatus.FORBIDDEN);
        }
        List<String> clientAuthLoans = clientAuth.getLoans().stream().map(Loan::getName).collect(Collectors.toList());
        if (clientAuthLoans.contains(loan.getName())) {
            return new ResponseEntity<>("Rejected you doesn't could have two loans of the same type ", HttpStatus.FORBIDDEN);
        }
        if (LoanApplicationDTO.getAmmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("Rejected amount, exceed the limits", HttpStatus.FORBIDDEN);
        }
        if (!loan.getPayments().contains(LoanApplicationDTO.getPayments())) {
            return new ResponseEntity<>("Rejected payments, exceed the limits", HttpStatus.FORBIDDEN);
        }

        if (!clientAuth.getAccounts().contains(destinyAccount)) {
            return new ResponseEntity<>("rejected the account muts be your", HttpStatus.FORBIDDEN);
        }
        Double priceLoan = LoanApplicationDTO.getAmmount() * loan.getInterestPercentage();
        ClientLoan newClientLoan = new ClientLoan(priceLoan.intValue(), LoanApplicationDTO.getPayments(), loan, clientAuth);
        Transaction newTransaction = new Transaction(TransactionType.CREDIT, LoanApplicationDTO.getAmmount(), loan.getName() + " loan approved", LocalDateTime.now());
        newTransaction.setAccountBalance(destinyAccount.getBalance() + LoanApplicationDTO.getAmmount());
        destinyAccount.setBalance(destinyAccount.getBalance() + LoanApplicationDTO.getAmmount());
        clientLoanRepository.save(newClientLoan);
        transactionService.saveTransaction(newTransaction);
        destinyAccount.addTransaction(newTransaction);
        accountsService.saveAccount(destinyAccount);
        return new ResponseEntity<>("success", HttpStatus.CREATED);

    }

    @PostMapping("/loans/admin")
    public ResponseEntity<Object> createNewLoans(@RequestParam String name, @RequestParam int maxAmount, @RequestParam List<Integer> payments, @RequestParam double interestPercentage, Authentication authentication) {
        if (name.isEmpty() || maxAmount <= 0 || payments.isEmpty() || interestPercentage <= 0) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        Client clientAuth = clientService.getCurrentClient(authentication);
        if (!clientAuth.getClientRole().equals("ADMIN")) {
            return new ResponseEntity<>("You do not have permission to do this", HttpStatus.FORBIDDEN);
        }
        Loan newLoan = new Loan(name, maxAmount, payments, interestPercentage);
        loanService.saveLoans(newLoan);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanService.getLoans();
    }

}