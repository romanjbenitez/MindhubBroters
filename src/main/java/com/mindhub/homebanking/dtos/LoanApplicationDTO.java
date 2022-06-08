package com.mindhub.homebanking.dtos;

import java.util.List;

public class LoanApplicationDTO {

    private Long id;
    private Double ammount;
    private int payments;
    private String accountNumber;

    public LoanApplicationDTO() {
    }

    public LoanApplicationDTO(Long id, Double ammount, int payments, String accountNumber) {
        this.id = id;
        this.ammount = ammount;
        this.payments = payments;
        this.accountNumber = accountNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
