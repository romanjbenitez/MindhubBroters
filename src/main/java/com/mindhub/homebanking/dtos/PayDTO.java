package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Pay;

public class PayDTO {
    private Long id;
    private String number;
    private int cvv;
    private Double ammount;
    private String description;

    public PayDTO() {
    }
    public PayDTO(Pay pay) {
        this.id = pay.getId();
        this.number = pay.getNumber();
        this.cvv = pay.getCvv();
        this.ammount = pay.getAmmount();
        this.description = pay.getDescription();
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
