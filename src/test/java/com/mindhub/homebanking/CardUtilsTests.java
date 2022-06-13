package com.mindhub.homebanking;

import com.mindhub.homebanking.services.CardsService;
import com.mindhub.homebanking.utils.CardUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class CardUtilsTests {
    @Autowired
    CardsService cardsService;
    @Test
    public void cardNumberIsCreated() {
        String cardNumber = CardUtils.getCardNumber(cardsService.getNumbersOfCards());
        assertThat(cardNumber, is(not(emptyOrNullString())));

    }
}
