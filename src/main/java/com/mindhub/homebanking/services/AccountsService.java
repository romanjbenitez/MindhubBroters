package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface AccountsService {

    List<AccountDTO> getAccounts();
    AccountDTO getAccount(Long id);
    Account getAccountById(Long id);
    void saveAccount(Account account);
    Set<String> getNumbersOfAccount();
    Account getAccountByNumber(String number);


}
