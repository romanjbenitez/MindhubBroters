package com.mindhub.homebanking.dtos;



import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;

public class ClientLoanDTO {
    private long id;
    private long loanID;

    private String name;
    private int payments;
    private int amount;


    public ClientLoanDTO() {
    }

    public ClientLoanDTO(ClientLoan clientLoan) {
        this.id = clientLoan.getId();
        this.loanID = clientLoan.getLoan().getId();
        this.name = clientLoan.getLoan().getName();
        this.payments = clientLoan.getPayments();
        this.amount = clientLoan.getAmount();

    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public int getPayments() {
        return payments;
    }
    public void setPayments(int payments) {
        this.payments = payments;
    }

    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }


    public long getLoanID() {return loanID;}
    public void setLoanID(long loanID) {this.loanID = loanID;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}
