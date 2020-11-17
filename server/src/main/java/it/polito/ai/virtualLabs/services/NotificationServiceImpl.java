package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.controllers.NotificationController;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.entities.Team;
import it.polito.ai.virtualLabs.entities.TeamToken;
import it.polito.ai.virtualLabs.exceptions.TokenNotFoundException;
import it.polito.ai.virtualLabs.repositories.TeamRepository;
import it.polito.ai.virtualLabs.repositories.TeamTokenRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@Transactional
@EnableScheduling
public class NotificationServiceImpl implements NotificationService{
    @Autowired
    public JavaMailSender emailSender;


    @Autowired
    TeamService teamService;

    @Autowired
    TeamTokenRepository tokenRepository;

    @Autowired
    ModelMapper modelMapper;


    @Autowired
    TeamRepository teamRepository;
    @Scheduled(fixedRate = 1000*60*60) // every hour
    public void cleanToken() {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        System.out.println("SCHEDULED TASK "+t);
        List<TeamToken> expired=tokenRepository.findAllByExpiryBefore(t);
        System.out.println("FOUNDED  "+expired.size()+" EXPIRED TOKEN");

        ArrayList<Long> teamsId = new ArrayList<Long>();

        expired.forEach(token ->{
            teamsId.add(token.getTeam().getId());
            tokenRepository.delete(token);
        });

        for (Long id:teamsId){
            try{
                if(teamRepository.existsById(id)){
                    teamRepository.deleteById(id);
                    System.out.println("Eliminato team "+id);
                }
            }catch(Exception e){
                System.out.println("Errore nel rimuovere il team "+e.toString());
                continue;
            }
        }
    }


    public void sendMessage(String address, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("s263138@studenti.polito.it");
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);

    }



    private Long checkToken(String token){ // controlla la validita del token, se valido lo elimina e ritorna il team id
        Optional<TeamToken> t=tokenRepository.findById(token);
        if(t.isEmpty()) {
            System.out.println("TOKEN NON ESISTE");
            throw  new TokenNotFoundException();
        }
        System.out.println("SCADENZA TOKEN "+t.get().getExpiryDate().toString());
        if(t.get().getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            System.out.println("TOKEN SCADUTO");
            throw  new TokenNotFoundException();
        }
        Long teamId=t.get().getTeam().getId();
        tokenRepository.delete(t.get()); // elimino il token
        return teamId;

    }

    @Override
    public boolean confirm(String token)
    {

        Long teamId=checkToken(token);
        if (teamId == null) return false;

        if(tokenRepository.findAllByTeamId(teamId).size() > 0) {
            System.out.println("Ci sono ancora token pendenti =(");
            return false;
        }

        teamService.activateTeam(teamId);
        return true;
    }

    @Override
    public boolean reject(String token) {
        Long teamId=checkToken(token);
        if (teamId == null) return false;

        List<TeamToken> list=tokenRepository.findAllByTeamId(teamId);

        list.forEach(t -> tokenRepository.delete(t));
        teamService.evictTeam(teamId);


        return true;
    }

    @Override
    @Async
    public void notifyTeam(TeamDTO teamDto, List<String> memberIds) {
        long now = System.currentTimeMillis();
        memberIds.forEach(id ->{
            TeamToken t = new TeamToken();
            t.setId(UUID.randomUUID().toString());
            t.setExpiryDate(new Timestamp(now +3600000));
            t.setStudentId(id);
            t.setTeam(modelMapper.map(teamDto, Team.class));
            //t.setTeamId(dto.getId());

            tokenRepository.save(t);

            String confirmLink = linkTo(NotificationController.class).slash("confirm").slash(t.getId()).toString();
            String rejectLink = linkTo(NotificationController.class).slash("reject").slash(t.getId()).toString();
            String subject="You have invited to join group "+ teamDto.getName();
            String text ="confirm:  "+confirmLink+"\n reject: "+rejectLink;

            sendMessage('s'+id+"@studenti.polito.it",subject,text);

        });

    }
}
