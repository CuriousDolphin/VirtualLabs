package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.UserDTO;
import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.entities.Teacher;
import it.polito.ai.virtualLabs.entities.User;
import it.polito.ai.virtualLabs.repositories.StudentRepository;
import it.polito.ai.virtualLabs.repositories.TeacherRepository;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import it.polito.ai.virtualLabs.security.AuthenticationRequest;
import it.polito.ai.virtualLabs.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    UserRepository users;
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    StudentRepository students;
    @Autowired
    TeacherRepository teachers;

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody AuthenticationRequest data) {
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username, this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());
            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }


    public User AddGenericUser(String username, String psw)
    {
        if(users.findByUsername(username).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists!");
        User u = User.builder().username(username)
                .password(passwordEncoder.encode(psw))
                .enabled(false)
                .build();

        users.save(u);
        u = users.findByUsername(username).get();
        return u;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid UserDTO data) {
        try {
            String username = data.getUsername();
            String psw = data.getPassword();
            User user;
            Optional<Student> optStudent;
            Student student;
            Optional<Teacher> optTeacher;
            Teacher teacher;
            if(username.endsWith("@studenti.polito.it"))
            {
                user = AddGenericUser(username,psw);
                String id = username.split("@")[0];
                optStudent = students.findByIdIgnoreCase(id);
                if(optStudent.isPresent())
                {
                    //student already exist, update the exist data with the parameters received trough the form.
                    //TODO: devo settare anche nome e cognome o solamente la mail?
                    student = optStudent.get();
                    student.setEmail(username);
                    student.setName(data.getName());
                    student.setLastName(data.getLastName());
                    students.save(student);
                }
                else
                {
                    //insert new student
                    student = Student.builder()
                            .id(id)
                            .email(username)
                            .name(data.getName())
                            .lastName(data.getLastName())
                            .build();
                    students.save(student);
                }
            }
            else if(username.endsWith("@polito.it"))
            {
                user = AddGenericUser(username,psw);
                String id = username.split("@")[0];
                optTeacher = teachers.findByIdIgnoreCase(id);
                System.out.println("sono vivo 2");
                if(optTeacher.isPresent())
                {
                    //TODO: può essere che u nteacher sia già esistente senza essere registrato come user? penso di no

                }
                else
                {
                    //insert new teacher
                    teacher = Teacher.builder()
                            .id(id)
                            .email(username)
                            .name(data.getName())
                            .lastName(data.getLastName())
                            .build();
                    teachers.save(teacher);
                    System.out.println("sono vivo 3");
                }
            }
            else
            {
                throw new BadCredentialsException("Invalid username/password supplied");
            }

            //TODO deve ritornare uno userDTO e devo fare la conversione da user a dto
            Map<Object, Object> model = new HashMap<>();
            model.put("username", user.getUsername());
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }
}
