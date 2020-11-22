package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.exceptions.TokenNotFoundException;
import it.polito.ai.virtualLabs.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;



    @GetMapping("/confirm/{token}")
    void confirm(@PathVariable String token) {
        String response;
        try {
            Boolean ris = this.notificationService.confirm(token);
            if (!ris) {
                System.out.println("LA CONFERMA  NON E' ANDATA A BUON FINE");
                response = "Team confermato! ma altri partecipanti devono ancora confermare";
            } else {
                System.out.println("LA CONFERMA  E' ANDATA A BUON FINE,GRUPPO ATTIVATO");
                response= "Team confermato! tutti i membri hanno confermato, gruppo attivato!";

            }
        } catch (TokenNotFoundException e) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found or expired");
        }


       // return response;
    }

    @GetMapping("/reject/{token}")
    void  reject(@PathVariable String token, Model m) {
        String response;

        try {
            Boolean ris = this.notificationService.reject(token);
            if (!ris) {
                System.out.println("TOKEN NON VALIDO");
               response="Token non esistente o scaduto";

            } else {
                System.out.println("LA DISDETTA e' ANDATA A BUON FINE");
                response="La disdetta  e' andata a buon fine, il gruppo e' stato eliminato";
            }
        } catch (TokenNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found or expired");
        }


      //  return response;
    }
}
