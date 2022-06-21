package com.mindhub.homebanking.controllers;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.services.AccountsService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.CardUtils.getRandomNumber;

@RestController
@RequestMapping("/api")
public class AccountsController {


    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountsService accountsService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountsService.getAccounts();
    }


    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {
        return accountsService.getAccount(id);
    }


    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createNewAccount(Authentication authentication, @RequestParam AccountType accountType) {
        if (clientService.getCurrentClient(authentication).getAccounts().stream().filter(account -> !account.getHidden()).collect(Collectors.toSet()).size() == 3) {
            return new ResponseEntity<Object>("you already have three accounts", HttpStatus.FORBIDDEN);
        }
        Set<String> numbersAccount = accountsService.getNumbersOfAccount();

        String randomNumber = "VIN" + getRandomNumber(10000000, 99999999);
        while (numbersAccount.contains(randomNumber)) {
            randomNumber = "VIN" + getRandomNumber(10000000, 99999999);
        }
        Account newAccount = new Account(randomNumber, LocalDateTime.now(), 0, accountType);

        clientService.getCurrentClient(authentication).addAccount(newAccount);
        accountsService.saveAccount(newAccount);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    @PostMapping("/clients/current/accounts/delete")
    public ResponseEntity<Object> deleteAccount(@RequestParam String number, Authentication authentication) {
        if (number.isEmpty()) {
            return new ResponseEntity<Object>("Missing data", HttpStatus.FORBIDDEN);
        }
        Client clientAuth = clientService.getCurrentClient(authentication);
        if (clientService.getClientByEmail(clientAuth.getEmail()) == null) {
            return new ResponseEntity<Object>("You not authenticated", HttpStatus.FORBIDDEN);
        }

        Account accountToDelete = accountsService.getAccountByNumber(number);
        if (!clientAuth.getAccounts().contains(accountToDelete)) {
            return new ResponseEntity<Object>("The account doesn't belongs to you", HttpStatus.FORBIDDEN);
        }
        Set<Transaction> transactionsToDelete = accountToDelete.getTransactions();
        accountToDelete.setHidden(true);
        transactionsToDelete.forEach(transaction -> transaction.setHidden(true));
        accountsService.saveAccount(accountToDelete);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/pdf/account")
    public ResponseEntity<Object> getPdfAccount(HttpServletResponse response, Authentication authentication, @RequestParam String from, @RequestParam String to, @RequestParam Long id) throws DocumentException, IOException, ParseException {

        if(from.isEmpty() || to.isEmpty() || id.toString().isEmpty()){
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

            response.setContentType("application/pdf");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String currentDateTime = dateFormatter.format(new Date());


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=mbb_tr_" + currentDateTime + ".pdf";

        response.setHeader(headerKey, headerValue);



        LocalDateTime fromFormat = LocalDateTime.parse(from, formatter);
        LocalDateTime toFormat = LocalDateTime.parse(to,formatter);
        Client clientAuth = clientService.getCurrentClient(authentication);
        Account currentAccount = accountsService.getAccountById(id);
        List<Transaction> listTransfers = transactionService.getTransactionsBetweenDates(fromFormat, toFormat).stream().filter(transaction -> transaction.getAccount().getId() == id).collect(Collectors.toList());

        if(toFormat.isAfter(LocalDateTime.now().plusHours(5))|| fromFormat.isAfter(LocalDateTime.now().plusHours(5))){
            return new ResponseEntity<>("The date entered must be less than the current date", HttpStatus.FORBIDDEN);
        }
        if(listTransfers.size() == 0){
            return new ResponseEntity<>("No transfer found in that date range", HttpStatus.LENGTH_REQUIRED);
        }

        Document pdf = new Document(PageSize.A4);
        PdfWriter.getInstance(pdf, response.getOutputStream());
        pdf.open();
        Font tittles = new Font(Font.TIMES_ROMAN, 24, Font.BOLD);
        Font body =  new Font(Font.TIMES_ROMAN, 14);
        Paragraph title = new Paragraph("Transfers of account number: " + currentAccount.getNumber(), tittles);
        Paragraph client = new Paragraph("Client : " + clientAuth.getFirstName() + " " + clientAuth.getLastName(), body);
        Paragraph email = new Paragraph("Email : " + clientAuth.getEmail(), body);
        Paragraph accountBalance = new Paragraph("Account balance " + currentAccount.getBalance() , body);
        Paragraph accountType = new Paragraph("Account type " + currentAccount.getAccountType() , body);
        Paragraph Date = new Paragraph("Date: " + LocalDateTime.now().format(formatter), body);

        pdf.add(title);
        pdf.add(Chunk.NEWLINE);
        pdf.add(client);
        pdf.add(email);
        pdf.add(Chunk.NEWLINE);
        pdf.add(accountBalance);
        pdf.add(accountType);
        pdf.add(Date);
        pdf.add(Chunk.NEWLINE);

        PdfPTable tableTransaction = new PdfPTable(5);
        tableTransaction.setWidthPercentage(100f);
        tableTransaction.setWidths(new float[] {1.0f, 2.5f, 1.5f, 3.5f, 1.5f});
        tableTransaction.setSpacingBefore(10);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.WHITE);
        cell.setPadding(10);

        cell.setPhrase(new Phrase("ID", body));
        tableTransaction.addCell(cell);

        cell.setPhrase(new Phrase("Date", body));
        tableTransaction.addCell(cell);

        cell.setPhrase(new Phrase("Type", body));
        tableTransaction.addCell(cell);

        cell.setPhrase(new Phrase("Description", body));
        tableTransaction.addCell(cell);

        cell.setPhrase(new Phrase("Amount", body));
        tableTransaction.addCell(cell);


        for (Transaction transaction : listTransfers){
            tableTransaction.addCell(String.valueOf(transaction.getId()));
            tableTransaction.addCell(String.valueOf(transaction.getDate().format(formatter)));
            tableTransaction.addCell(String.valueOf(transaction.getType()));
            tableTransaction.addCell(transaction.getDescription());
            tableTransaction.addCell(String.valueOf(transaction.getAmount()));
        }

        pdf.add(tableTransaction);
        pdf.close();

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

}
