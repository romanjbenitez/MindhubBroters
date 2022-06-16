package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Client;

import java.util.Set;

import static java.util.stream.Collectors.toSet;


public class ClientDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<AccountDTO> accounts;
    private Set<ClientLoanDTO> loans;
    private Set<CardDTO> cards;
    private String imgProfile;
    private String clientRole;
    public ClientDTO() {
    }

    public ClientDTO(Client client) {

        this.id = client.getId();

        this.firstName = client.getFirstName();

        this.lastName = client.getLastName();

        this.email = client.getEmail();

        this.accounts = client.getAccounts().stream().map(AccountDTO::new).filter(accountDTO -> !accountDTO.isHidden()).collect(toSet());

        this.loans = client.getClientLoans().stream().map(ClientLoanDTO::new).collect(toSet());

        this.cards = client.getCards().stream().map(CardDTO::new).filter(cardDTO -> !cardDTO.getHidden()).collect(toSet());
        this.imgProfile = client.getImgProfile();
        this.clientRole = client.getClientRole();
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<AccountDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<AccountDTO> accounts) {
        this.accounts = accounts;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public Set<ClientLoanDTO> getLoans() {
        return loans;
    }

    public void setLoans(Set<ClientLoanDTO> loans) {
        this.loans = loans;
    }

    public Set<CardDTO> getCards() {
        return cards;
    }

    public void setCards(Set<CardDTO> loans) {
        this.cards = cards;
    }

    public String getClientRole() {
        return clientRole;
    }
}
