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
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public ResponseEntity<Object> changeSettings(@RequestParam("file") MultipartFile image, Authentication authentication) {
        if(image.isEmpty()){
            return new ResponseEntity<>("You must to submit an image",HttpStatus.FORBIDDEN);
        }
        Path pathImgProfiles = Paths.get("src//main//resources//static/assets/usersProfiles");
        String pathAbsolute = pathImgProfiles.toFile().getAbsolutePath();
        Client currentClient = repo.findByEmail(authentication.getName());
        Path pathComplete = Paths.get(pathAbsolute + "//" +  image.getOriginalFilename());
        if(currentClient.getImgProfile()!= null){
            try {
                Files.delete(Paths.get(pathAbsolute + "//" + currentClient.getImgProfile() ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            byte[] bytesImg = image.getBytes();
            Files.write(pathComplete,bytesImg );
            currentClient.setImgProfile(image.getOriginalFilename());
            repo.save(currentClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


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
