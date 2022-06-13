package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface CardsService {
    Set<String> getNumbersOfCards();
    Card getCardByNumber(String number);
    void saveCard(Card card);

}
