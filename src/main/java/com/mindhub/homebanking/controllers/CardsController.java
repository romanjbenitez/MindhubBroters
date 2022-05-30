package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    private ClientRepository repo;
    @Autowired
    private CardRepository cardRepository;


    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> addNewCard(
            @RequestParam CardColor color, @RequestParam CardType type, Authentication authentication ){
        if(repo.findByEmail(authentication.getName()).getCards().stream().filter(card -> card.getType().equals(DEBIT)).count() == 3 && type == DEBIT){
            return new ResponseEntity<Object>("You already have three debit cards", HttpStatus.FORBIDDEN);
        }
        else if(repo.findByEmail(authentication.getName()).getCards().stream().filter(card -> card.getType().equals(CREDIT)).count() == 3 && type == CREDIT){
            return new ResponseEntity<Object>("You already have three credit cards", HttpStatus.FORBIDDEN);
        }

        Set<String> numbersOfCards = cardRepository.findAll().stream().map(Card::getNumber).collect(Collectors.toSet());
        String cardNumber = getRandomNumber(1000 , 9999) + "-" +
                            getRandomNumber(1000 , 9999) + "-" +
                            getRandomNumber(1000 , 9999) + "-" +
                            getRandomNumber(1000 , 9999);
        while(numbersOfCards.contains(cardNumber)){
            cardNumber =getRandomNumber(1000 , 9999) + "-" +
                        getRandomNumber(1000 , 9999) + "-" +
                        getRandomNumber(1000 , 9999) + "-" +
                        getRandomNumber(1000 , 9999);
        }
        String cardHolder = repo.findByEmail(authentication.getName()).getFirstName() + repo.findByEmail(authentication.getName()).getLastName();
        Card newCard = new Card(type , color,cardHolder ,cardNumber,getRandomNumber(100, 999) , LocalDateTime.now(), LocalDateTime.now().plusYears(5));
        repo.findByEmail(authentication.getName()).addCard(newCard);
        cardRepository.save(newCard);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }
    public int getRandomNumber(int min, int max) {
        return (int)((Math.random() * (max - min)) + min);
    }
}
