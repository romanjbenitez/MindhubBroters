package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientService clientService;

    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientService.getListClientDto();
    }

    @PostMapping("/clients")
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<Object>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (clientService.getClientByEmail(email) != null) {
            return new ResponseEntity<Object>("Name already in use", HttpStatus.FORBIDDEN);
        }
        clientService.saveClient(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @PostMapping("/clients/settings")
    public ResponseEntity<Object> changeSettings(@RequestParam("file") MultipartFile image, Authentication authentication) {
        if (image.isEmpty()) {
            return new ResponseEntity<>("You must to submit an image", HttpStatus.FORBIDDEN);
        }

        Path pathImgProfiles = Paths.get("src//main//resources//static/assets/usersProfiles");
        String pathAbsolute = pathImgProfiles.toFile().getAbsolutePath();
        Client currentClient = clientService.getCurrentClient(authentication);
        Path pathComplete = Paths.get(pathAbsolute + "//" + image.getOriginalFilename());
        if (currentClient.getImgProfile() != null) {
            try {
                Files.delete(Paths.get(pathAbsolute + "//" + currentClient.getImgProfile()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            byte[] bytesImg = image.getBytes();
            Files.write(pathComplete, bytesImg);
            currentClient.setImgProfile(image.getOriginalFilename());
            clientService.saveClient(currentClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return new ResponseEntity<>("Change your profile", HttpStatus.CREATED);
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id) {
        return clientService.getClient(id);
    }

    @GetMapping("/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication) {
        return new ClientDTO(clientService.getCurrentClient(authentication));
    }

    @PatchMapping("/clients/current/update")
    public ResponseEntity<Object> updateClient(@RequestParam String firstName, @RequestParam String lastName,
                                               Authentication authentication) {
        if (firstName.isEmpty() || lastName.isEmpty()) {
            return new ResponseEntity<Object>("Missing data", HttpStatus.FORBIDDEN);
        }
        Client clientAuth = clientService.getCurrentClient(authentication);
        clientAuth.setFirstName(clientAuth.getFirstName().equals(firstName) ? clientAuth.getFirstName() : firstName);
        clientAuth.setLastName(clientAuth.getLastName().equals(lastName) ? clientAuth.getLastName() : lastName);
        clientService.saveClient(clientAuth);
        return new ResponseEntity<Object>("Success", HttpStatus.CREATED);
    }
    ;
}
