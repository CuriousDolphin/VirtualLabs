package it.polito.ai.lab3.services;

import it.polito.ai.lab3.dtos.TeamDTO;

import java.util.List;

public interface NotificationService {
    void sendMessage(String address,String subject,String body);

    boolean confirm(String token); // per confermare la partecipazione al gruppo
    boolean reject(String token); //per esprimere il proprio diniego a partecipare
    void notifyTeam(TeamDTO dto, List<String> memberIds);
}
