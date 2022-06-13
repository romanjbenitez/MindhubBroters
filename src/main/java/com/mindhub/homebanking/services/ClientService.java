package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    List<ClientDTO> getListClientDto();
    ClientDTO getClient(Long id);
    Client getCurrentClient(Authentication authentication);
    Client getClientByEmail(String email);
    void saveClient(Client client);

    interface LoanService {
    }
}
