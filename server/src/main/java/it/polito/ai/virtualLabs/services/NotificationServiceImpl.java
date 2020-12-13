package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.controllers.NotificationController;
import it.polito.ai.virtualLabs.controllers.NotificationRegistrationController;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.dtos.UserDTO;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.exceptions.TokenNotFoundException;
import it.polito.ai.virtualLabs.repositories.RegistrationTokenRepository;
import it.polito.ai.virtualLabs.repositories.StudentRepository;
import it.polito.ai.virtualLabs.repositories.TeamRepository;
import it.polito.ai.virtualLabs.repositories.TokenTeamRepository;
import lombok.Builder;
import lombok.Data;
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
    TokenTeamRepository tokenRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    RegistrationTokenRepository registrationRepository;


    @Autowired
    TeamRepository teamRepository;
    @Scheduled(fixedRate = 1000*60*60) // every hour
    public void cleanToken() {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        System.out.println("SCHEDULED TASK "+t);
        List<TokenTeam> expired=tokenRepository.findAllByExpiryBefore(t);
        System.out.println("FOUNDED  "+expired.size()+" EXPIRED TEAM TOKEN");

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

        List<RegistrationToken> expiredRegistration =registrationRepository.findAllByExpiryBefore(t);
        System.out.println("FOUNDED  "+expiredRegistration.size()+" EXPIRED USER TOKEN");



        ArrayList<String> usersId = new ArrayList<String>();

        expiredRegistration.forEach(token ->{
            usersId.add(token.getUser().getId());
            registrationRepository.delete(token);
        });

        for (String id:usersId){
            try{
                if(registrationRepository.existsById(id)){
                    registrationRepository.deleteById(id);
                    System.out.println("Eliminato user "+id);
                }
            }catch(Exception e){
                System.out.println("Errore nel rimuovere l'utente "+e.toString());
                continue;
            }
        }

    }


    public void sendMessage(String address, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("progetto.webapp.dreamteam@gmail.com");
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);

    }



    private StudentTeamPair checkToken(String token){ // controlla la validita del token, se valido lo elimina,aggiunge il team allo studente e ritorna il team id
        Optional<TokenTeam> t = tokenRepository.findById(token);
        if(t.isEmpty()) {
            System.out.println("TOKEN NON ESISTE");
            throw  new TokenNotFoundException();
        }
        System.out.println("SCADENZA TOKEN "+t.get().getExpiryDate().toString());
        if(t.get().getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            System.out.println("TOKEN SCADUTO");
            throw  new TokenNotFoundException();
        }

       // Long teamId=t.get().getTeam().getId();
        String studentId  = t.get().getStudentId();
        Student student=studentRepository.findByIdIgnoreCase(studentId).get();
        Team team=t.get().getTeam();

        tokenRepository.delete(t.get()); // elimino il token
        return StudentTeamPair.builder().student(student).team(team).build();
        //return new Pair<Student,Team>(student,team);

    }

    @Override
    public boolean confirm(String token)
    {
        //Pair<Student,Team> res= checkToken(token);
        StudentTeamPair res=checkToken(token);
        Student s=res.getStudent();
        Team team=res.getTeam();
        Long teamId =team.getId();
        s.addTeam(team);

        if (teamId == null) return false;

        if(tokenRepository.findAllByTeamId(teamId).size() > 0) {
            System.out.println("Ci sono ancora token pendenti =(");
            return false;
        }else{ // tutti hanno confermato, attivo il team
            teamService.activateTeam(teamId);
            return true;
        }

    }

    @Override
    public boolean reject(String token) {
        Long teamId=checkToken(token).getTeam().getId();
        if (teamId == null) return false;

        List<TokenTeam> list=tokenRepository.findAllByTeamId(teamId);

        list.forEach(t -> tokenRepository.delete(t));
        teamService.evictTeam(teamId);


        return true;
    }

    @Override
    public void notifyTeam(TeamDTO teamDto, List<String> memberIds,Integer timeoutDays) {
        long now = System.currentTimeMillis();
        memberIds.forEach(id ->{
            TokenTeam t = new TokenTeam();
            t.setId(UUID.randomUUID().toString());
            t.setExpiryDate(new Timestamp(now +timeoutDays*24*3600*1000));
            t.setStudentId(id);
            t.setTeam(modelMapper.map(teamDto, Team.class));
            //t.setTeamId(dto.getId());

            tokenRepository.save(t);

            /* String confirmLink = linkTo(NotificationController.class).slash("confirm").slash(t.getId()).toString();
            String rejectLink = linkTo(NotificationController.class).slash("reject").slash(t.getId()).toString();
            String subject="You have invited to join group "+ teamDto.getName();
            String text ="confirm:  "+confirmLink+"\n reject: "+rejectLink;

            sendMessage('s'+id+"@studenti.polito.it",Ssubject,text); */

        });

    }

    @Override
    @Async
    public void notifyRegistration(UserDTO user) {
        long now = System.currentTimeMillis();
        String id = user.getId();
        System.out.println(id);
        RegistrationToken t = new RegistrationToken();
        t.setId(UUID.randomUUID().toString());
        t.setExpiryDate(new Timestamp(now +3600000));
        t.setUser(modelMapper.map(user, User.class));
        registrationRepository.save(t);
        String confirmLink = linkTo(NotificationRegistrationController.class).slash("confirm_registration").slash(t.getId()).toString();
        String subject="VirtualLabs registration";
        String text =   "We have recived a registration attempt of a user with id number:" + id + "\n" +
                        "If it's your id, please confirm your registration by clicking this link: "+confirmLink + "\n" +
                        "If this is not your id number, please ignore this email.";
        sendMessage('s'+id+"@studenti.polito.it",subject,text);
    }

    @Override
    public boolean confirmRegistration(String token)
    {
        boolean res = true;
        try {
            Optional<RegistrationToken> foundToken = registrationRepository.findById(token);
            if (foundToken.isEmpty()) {
                System.out.println("TOKEN NON ESISTE");
                throw new TokenNotFoundException();
            }
            System.out.println("SCADENZA TOKEN " + foundToken.get().getExpiryDate().toString());
            //token scaduto, lo elimino e lancio eccezione
            if (foundToken.get().getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
                System.out.println("TOKEN SCADUTO");
                registrationRepository.delete(foundToken.get());
                throw new TokenNotFoundException();
            }
            User user = foundToken.get().getUser();
            user.setEnabled(true);

            registrationRepository.delete(foundToken.get()); // elimino il token
        }
        catch(TokenNotFoundException e)
        {
            res = false;
        }
        return res;
    }
}

@Builder
@Data
class StudentTeamPair{
    Student student;
    Team team;
}