package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.CardsService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.models.CardType.CREDIT;
import static com.mindhub.homebanking.models.CardType.DEBIT;

@RestController
@RequestMapping("/api")
public class CardsController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private CardsService cardsService;


    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> addNewCard(
            @RequestParam CardColor color, @RequestParam CardType type, Authentication authentication) {
        if(color.toString().isEmpty() || type.toString().isEmpty()){
            if(color.toString().isEmpty()){
                return new ResponseEntity<Object>("Missing color", HttpStatus.FORBIDDEN);
            }
            if(type.toString().isEmpty()){
                return new ResponseEntity<Object>("Missing type", HttpStatus.FORBIDDEN);
            }
        }
        Client clientAuth = clientService.getCurrentClient(authentication);
        if (clientAuth.getCards().stream().filter(card -> card.getType().equals(DEBIT)).count() == 3 && type == DEBIT) {
            return new ResponseEntity<Object>("You already have three debit cards", HttpStatus.FORBIDDEN);
        } else if (clientAuth.getCards().stream().filter(card -> card.getType().equals(CREDIT)).count() == 3 && type == CREDIT) {
            return new ResponseEntity<Object>("You already have three credit cards", HttpStatus.FORBIDDEN);
        }

        Set<String> numbersOfCards = cardsService.getNumbersOfCards();
        String cardNumber = getRandomNumber(1000, 9999) + "-" +
                getRandomNumber(1000, 9999) + "-" +
                getRandomNumber(1000, 9999) + "-" +
                getRandomNumber(1000, 9999);
        while (numbersOfCards.contains(cardNumber)) {
            cardNumber = getRandomNumber(1000, 9999) + "-" +
                    getRandomNumber(1000, 9999) + "-" +
                    getRandomNumber(1000, 9999) + "-" +
                    getRandomNumber(1000, 9999);
        }
        String cardHolder = clientAuth.getFirstName() + clientAuth.getLastName();
        Card newCard = new Card(type, color, cardHolder, cardNumber, getRandomNumber(100, 999), LocalDateTime.now(), LocalDateTime.now().plusYears(5));
        clientAuth.addCard(newCard);
        cardsService.saveCard(newCard);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }
    @PostMapping("/clients/current/cards/delete")
    public ResponseEntity<Object> deleteCard(@RequestParam String cardNumber, Authentication authentication){
        if (cardNumber.isEmpty()){
            return new ResponseEntity<Object>("Missing card number", HttpStatus.FORBIDDEN);
        }
        Client clientAuth = clientService.getCurrentClient(authentication);
        if(!clientAuth.getCards().stream().map(Card::getNumber).collect(Collectors.toSet()).contains(cardNumber)){
            return new ResponseEntity<Object>("The card number doesn't belongs to you", HttpStatus.FORBIDDEN);
        }
        Card cardToDelete = cardsService.getCardByNumber(cardNumber);
        System.out.println(cardToDelete);
        cardToDelete.setHidden(true);
        cardsService.saveCard(cardToDelete);
       return new ResponseEntity<>("Deleted",HttpStatus.CREATED);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
