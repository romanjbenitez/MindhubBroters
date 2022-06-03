package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    private ClientRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping("/clients")
    public List<ClientDTO> getClients() {
        return repo.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<Object>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (repo.findByEmail(email) != null) {
            return new ResponseEntity<Object>("Name already in use", HttpStatus.FORBIDDEN);
        }
        repo.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @RequestMapping(path = "/clients/settings" , method = RequestMethod.POST)
    public ResponseEntity<Object> changeSettings(@RequestParam String imgProfile, Authentication authentication) {
        if(imgProfile.isEmpty()){
            return new ResponseEntity<>("Change your profile",HttpStatus.FORBIDDEN);
        }
        byte[] decodedByte = Base64.decodeBase64(imgProfile);
        Client currentClient = repo.findByEmail(authentication.getName());
        currentClient.setImgProfile(imgProfile);
        repo.save(currentClient);

        return new ResponseEntity<>("Change your profile",HttpStatus.CREATED);
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id) {
        return repo.findById(id).map(ClientDTO::new).orElse(null);
    }

    @RequestMapping("/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication) {
        return new ClientDTO(repo.findByEmail(authentication.getName()));
    }

    ;
}
