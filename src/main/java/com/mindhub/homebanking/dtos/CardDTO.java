package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class CardDTO {
    private long id;
    private CardType type;
    private CardColor color;
    private String cardHolder;
    private String number;
    private int cvv;
    private LocalDateTime fromDate;
    private LocalDateTime thruDate;

    public CardDTO() {
    }

    public CardDTO(Card card) {
        this.id = card.getId();
        this.type = card.getType();
        this.color = card.getColor();
        this.cardHolder= card.getCardHolder();
        this.cvv = card.getCvv();
        this.number = card.getNumber();
        this.fromDate = card.getFromDate();
        this.thruDate = card.getThruDate();
    }


    public long getId() {
        return id;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
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

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getThruDate() {
        return thruDate;
    }

    public void setThruDate(LocalDateTime thruDate) {
        this.thruDate = thruDate;
    }
}
