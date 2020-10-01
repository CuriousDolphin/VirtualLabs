package it.polito.ai.lab3.controllers;

import it.polito.ai.lab3.exceptions.TokenNotFoundException;
import it.polito.ai.lab3.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;



    @GetMapping("/confirm/{token}")
    public String confirm(@PathVariable String token, Model m) {
        try {
            Boolean ris = this.notificationService.confirm(token);
            if (!ris) {
                System.out.println("LA CONFERMA  NON E' ANDATA A BUON FINE");
                m.addAttribute("message", "Altri partecipanti devono ancora confermare");
            } else {
                System.out.println("LA CONFERMA  E' ANDATA A BUON FINE,GRUPPO ATTIVATO");
                m.addAttribute("message", "LA CONFERMA  E' ANDATA A BUON FINE,GRUPPO ATTIVATO");

            }
        } catch (TokenNotFoundException e) {
            m.addAttribute("message", "Token non valido o scaduto");
        }


        return "confirm";
    }

    @GetMapping("/reject/{token}")
    public String reject(@PathVariable String token, Model m) {
        try {
            Boolean ris = this.notificationService.reject(token);
            if (!ris) {
                System.out.println("TOKEN NON VALIDO");
                m.addAttribute("message", "Token non esistente o scaduto");
            } else {
                System.out.println("LA DISDETTA e' ANDATA A BUON FINE");
                m.addAttribute("message", "La disdetta  e' andata a buon fine, il gruppo e' stato eliminato");
            }
        } catch (TokenNotFoundException e) {
            m.addAttribute("message", "Token non valido o scaduto");
        }


        return "reject";
    }
}
