package com.mindhub.homebanking.utils;

import java.util.Set;

public final class CardUtils {

    private CardUtils() {
    }

    public static int getCvv() {
        return getRandomNumber(100, 999);
    }
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static String getCardNumber(Set<String> numbersOfCards) {
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
        return cardNumber;
    }
}
