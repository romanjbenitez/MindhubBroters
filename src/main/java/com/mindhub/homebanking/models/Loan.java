package com.mindhub.homebanking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String name;
    private int maxAmount;
    private double interestPercentage;
    @OneToMany(mappedBy="client", fetch=FetchType.EAGER)
    private Set<ClientLoan> clientLoans;
    @ElementCollection
    @Column(name="payments")
    private List<Integer> payments = new ArrayList<>();

    public Loan() {
    }

    public Loan(String name, int maxAmount, List<Integer> payments, double interestPercentage) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
        this.interestPercentage = interestPercentage;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getMaxAmount() {
        return maxAmount;
    }
    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }
    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }

    public void addClientLoan(ClientLoan clientLoan) {
        clientLoan.setLoan(this);
        clientLoans.add(clientLoan);
    }

    public double getInterestPercentage() {
        return interestPercentage;
    }

    public void setInterestPercentage(double interestPercentage) {
        this.interestPercentage = interestPercentage;
    }

    @JsonIgnore
    public List<Client> getClients() {
        return clientLoans.stream().map(ClientLoan::getClient).collect(toList());
    }
}
