package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Loan;

import java.util.List;

public class LoanDTO {
    private long id;
    private String name;
    private int maxAmount;
    private List<Integer> payments;
    private double interestPercentage;
    public LoanDTO() {
    }

    public LoanDTO(Loan loan) {
        this.id = loan.getId();
        this.name = loan.getName();
        this.maxAmount = loan.getMaxAmount();
        this.payments = loan.getPayments();
        this.interestPercentage = loan.getInterestPercentage();
    }

    public long getId() {
        return id;
    }

    public double getInterestPercentage() {
        return interestPercentage;
    }

    public String getName() {
        return name;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }
}
