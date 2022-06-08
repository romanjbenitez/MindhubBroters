package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static com.mindhub.homebanking.models.CardColor.*;
import static com.mindhub.homebanking.models.CardType.CREDIT;
import static com.mindhub.homebanking.models.CardType.DEBIT;


@SpringBootApplication
public class HomebankingApplication {


    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }

    @Autowired
    private PasswordEncoder passwordEnconder;


    @Bean
    public CommandLineRunner initData(ClientRepository repository,
                                      AccountRepository repository2,
                                      TransactionRepository repository3,
                                      LoanRepository repository4,
                                      ClientLoanRepository repository5,
                                      CardRepository repository6) {
        return (args) -> {



            Client melba = new Client("Melba", "Morel", "melba@mindhub.com",passwordEnconder.encode("melba"));
            Client client2 = new Client("Roman", "Benitez", "roman@mindhub.com",passwordEnconder.encode("1234"));

            LocalDateTime localDateNow = LocalDateTime.now();
            Account account1 = new Account("VIN001", localDateNow, 5000.00);
            Account account2 = new Account("VIN002", localDateNow.plusDays(1), 7500.00);
            Account account3 = new Account("VIN003", localDateNow, 500.00);

            Transaction transaction1 = new Transaction(TransactionType.DEBIT, -2000, "Cuota Mindhub", localDateNow);
            Transaction transaction2 = new Transaction(TransactionType.CREDIT, 4000, "Herencia", localDateNow);
            Transaction transaction3 = new Transaction(TransactionType.CREDIT, 7000, "Cobro alquiler", localDateNow);
            Transaction transaction4 = new Transaction(TransactionType.CREDIT, 7000, "Deposito Bancario", localDateNow);
            Transaction transaction5 = new Transaction(TransactionType.DEBIT, -7000, "Cuota Mindhub", localDateNow);
            List<Integer> cuotasHipotecario = List.of(12, 24, 36, 48, 60);
            List<Integer> cuotasPersonal = List.of(6, 12, 24);
            List<Integer> cuotasAutomotriz = List.of(6, 12, 24, 36);

            Loan mortgage = new Loan("Mortgage", 500000, cuotasHipotecario);
            Loan personal = new Loan("Personal", 100000, cuotasPersonal);
            Loan automotive = new Loan("Automotive", 300000, cuotasAutomotriz);

            ClientLoan clientLoanOne = new ClientLoan(400000, 60, mortgage, melba);
            ClientLoan clientLoanTwo = new ClientLoan(50000, 12, personal, melba);
            ClientLoan clientLoanThree = new ClientLoan(100000, 24, personal, client2);
            ClientLoan clientLoanFour = new ClientLoan(200000, 36, automotive, client2);

            Card cardOne = new Card(DEBIT, GOLD, "Melba Morel",
                            "2342-2345-4324-3214", 217, localDateNow,
                                    localDateNow.plusYears(5));
            Card cardTwo= new Card(CREDIT, TITANIUM, melba.getFirstName() + " " + melba.getLastName(),
                    "1234-4323-4324-9827", 179, localDateNow,
                    localDateNow.plusYears(5));

            Card cardThree= new Card(CREDIT, SILVER, client2.getFirstName() + " " + client2.getLastName(),
                    "5294-4223-4364-9821", 267, localDateNow,
                    localDateNow.plusYears(5));

            melba.addAccount(account1);
            melba.addAccount(account2);

            melba.addCard(cardOne);
            melba.addCard(cardTwo);

            account1.addTransaction(transaction1);
            account1.addTransaction(transaction2);
            account1.addTransaction(transaction3);
            account2.addTransaction(transaction5);

            client2.addAccount(account3);
            client2.addCard(cardThree);
            account3.addTransaction(transaction4);


            repository.save(melba);
            repository.save(client2);
            repository2.save(account1);
            repository2.save(account2);
            repository2.save(account3);
            repository3.save(transaction1);
            repository3.save(transaction2);
            repository3.save(transaction3);
            repository3.save(transaction4);
            repository3.save(transaction5);
            repository4.save(mortgage);
            repository4.save(personal);
            repository4.save(automotive);
            repository5.save(clientLoanOne);
            repository5.save(clientLoanTwo);
            repository5.save(clientLoanThree);
            repository5.save(clientLoanFour);
            repository6.save(cardOne);
            repository6.save(cardTwo);
            repository6.save(cardThree);
        };
    }
}



