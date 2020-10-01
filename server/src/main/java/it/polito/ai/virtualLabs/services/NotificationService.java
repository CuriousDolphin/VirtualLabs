package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.TeamDTO;

import java.util.List;

public interface NotificationService {
    void sendMessage(String address,String subject,String body);

    boolean confirm(String token); // per confermare la partecipazione al gruppo
    boolean reject(String token); //per esprimere il proprio diniego a partecipare
    void notifyTeam(TeamDTO dto, List<String> memberIds);
}
