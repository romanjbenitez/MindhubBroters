package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
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
public class LoanCrontroller {
    @Autowired
    private ClientRepository repo;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    ClientLoanRepository clientLoanRepository;

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> addLoan(@RequestBody LoanApplicationDTO LoanApplicationDTO, Authentication authentication) {
        if (LoanApplicationDTO.getId() > 3 || LoanApplicationDTO.getPayments() <= 0 || LoanApplicationDTO.getAccountNumber().isEmpty() || LoanApplicationDTO.getAmmount().toString().isEmpty()) {
            return new ResponseEntity<>("Missing DATA", HttpStatus.FORBIDDEN);
        }

        String name = LoanApplicationDTO.getId() == 1 ?"Mortgage":LoanApplicationDTO.getId()==2?"Personal":"Automotive";
        Loan loan = loanRepository.findByName(name);
        Account destinyAccount = accountRepository.findByNumber(LoanApplicationDTO.getAccountNumber());
        Client clientAuth = repo.findByEmail(authentication.getName());
        if (loan == null) {
            return new ResponseEntity<>("Invalid Loan", HttpStatus.FORBIDDEN);}
            if (LoanApplicationDTO.getAmmount() > loan.getMaxAmount()) {
                return new ResponseEntity<>("Rejected amount, exceed the limits", HttpStatus.FORBIDDEN);
            }
            if (!loan.getPayments().contains(LoanApplicationDTO.getPayments())) {
                return new ResponseEntity<>("Rejected payments, exceed the limits", HttpStatus.FORBIDDEN);
            }
            if (clientAuth == null) {
                return new ResponseEntity<>("rejected the account muts be your", HttpStatus.FORBIDDEN);
            }
            if (!clientAuth.getAccounts().contains(destinyAccount)) {
                return new ResponseEntity<>("rejected the account muts be your", HttpStatus.FORBIDDEN);
            }
            List<String> clientAuthLoans = clientAuth.getLoans().stream().map(clientLoan-> clientLoan.getName()).collect(Collectors.toList());
            if(clientAuthLoans.contains(loan.getName())){
                return new ResponseEntity<>("Rejected you doesn't could have two loans of the same type ", HttpStatus.FORBIDDEN);
            }
            Double priceLoan = LoanApplicationDTO.getAmmount() * 1.2;
            ClientLoan newClientLoan = new ClientLoan(priceLoan.intValue(), LoanApplicationDTO.getPayments(), loan, clientAuth);
            Transaction newTransaction = new Transaction(TransactionType.CREDIT, LoanApplicationDTO.getAmmount(), loan.getName() + " loan approved", LocalDateTime.now());
            destinyAccount.setBalance(destinyAccount.getBalance() + LoanApplicationDTO.getAmmount());
            clientLoanRepository.save(newClientLoan);

            transactionRepository.save(newTransaction);
            destinyAccount.addTransaction(newTransaction);
            accountRepository.save(destinyAccount);
            return new ResponseEntity<>("success", HttpStatus.CREATED);

        }

        @GetMapping("/loans")
        public List<LoanDTO> getLoans(){
            return loanRepository.findAll().stream().map(LoanDTO::new).collect(Collectors.toList());
        }

    }