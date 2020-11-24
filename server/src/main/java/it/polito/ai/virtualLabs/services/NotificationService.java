package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.dtos.UserDTO;

import java.util.List;

public interface NotificationService {
    void sendMessage(String address,String subject,String body);

    boolean confirm(String token); // per confermare la partecipazione al gruppo
    boolean reject(String token); //per esprimere il proprio diniego a partecipare
    void notifyTeam(TeamDTO dto, List<String> memberIds);
    void notifyRegistration(UserDTO user);
    boolean confirmRegistration(String token);
}
