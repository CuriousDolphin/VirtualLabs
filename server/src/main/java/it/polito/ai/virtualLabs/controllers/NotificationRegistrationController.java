package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.exceptions.TokenNotFoundException;
import it.polito.ai.virtualLabs.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confirm")
public class NotificationRegistrationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm_registration/{token}")
    public String confirmRegistration(@PathVariable String token, Model m) {
        try {

            Boolean ris = this.notificationService.confirmRegistration(token);
            System.out.println("TOKEN END");
            if (!ris) {
                System.out.println("LA REGISTRAZIONE  NON E' ANDATA A BUON FINE");
                m.addAttribute("message", "la registrazione non è andata a buon fine.\nToken scaduto o non valido.");
            } else {
                System.out.println("LA REGISTRAZIONE E' ANDATA A BUON FINE");
                m.addAttribute("message", "la registrazione è andata a buon fine.\nUtente attivato.");

            }
        } catch (TokenNotFoundException e) {
            m.addAttribute("message", "Token non valido o scaduto");
        }


        return "confirmed";
    }
}
