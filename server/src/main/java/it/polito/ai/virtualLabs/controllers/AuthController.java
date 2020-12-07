package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.UserProposal;
import it.polito.ai.virtualLabs.dtos.UserDTO;
import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.entities.Teacher;
import it.polito.ai.virtualLabs.entities.User;
import it.polito.ai.virtualLabs.repositories.StudentRepository;
import it.polito.ai.virtualLabs.repositories.TeacherRepository;
import it.polito.ai.virtualLabs.repositories.CourseRepository;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import it.polito.ai.virtualLabs.security.AuthenticationRequest;
import it.polito.ai.virtualLabs.security.JwtTokenProvider;
import it.polito.ai.virtualLabs.services.NotificationService;
import org.modelmapper.ModelMapper;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/auth")
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
    @Autowired
    NotificationService notificationService;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest data) {
        try {
            String email = data.getUsername();
            String id ="";
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, data.getPassword()));
            if(email.split("@")[1].compareTo("studenti.polito.it") == 0)
            {
                Optional<Student> s=studentRepository.findByEmailIgnoreCase(email);
                if(s.isPresent())
                    id=s.get().getId();
            }
            else
            {
                Optional<Teacher> d = teachers.findByEmailIgnoreCase(email);
                if(d.isPresent())
                    id=d.get().getId();
            }

            String token = jwtTokenProvider.createToken(
                    email,
                    this.users.findByUsername(email)
                            .orElseThrow(() -> new UsernameNotFoundException("Username " + email + "not found")).getRoles(),id);
            Map<Object, Object> model = new HashMap<>();
            model.put("username", email);
            model.put("token", token);
            //model.put("userId", id);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }


    public User AddGenericUser(String username, String psw)
    {
        if(users.findByUsername(username).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists!");
        User u = User.builder().id(username.split("@")[0])
                .username(username)
                .password(passwordEncoder.encode(psw))
                .enabled(false)
                .roles(username.toLowerCase().startsWith("d") ? Arrays.asList( "ROLE_PROF") : Arrays.asList( "ROLE_STUDENT"))
                .build();

        users.save(u);
        u = users.findByUsername(username).get();
        return u;
    }

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody @Valid UserProposal data) {
        try {
            String username = data.getUsername();
            String psw = data.getPassword();
            User user;
            Optional<Student> optStudent;
            Student student;
            Optional<Teacher> optTeacher;
            Teacher teacher;

            if(!(data.getPassword().equals(data.getConfirmPassword())))
            {
                throw new BadCredentialsException("Passwords not match");
            }
            if(username.endsWith("@studenti.polito.it") && username.startsWith("s"))
            {
                user = AddGenericUser(username,psw);
                String id = username.split("@")[0];
                optStudent = students.findByIdIgnoreCase(id);
                if(optStudent.isPresent())
                {
                    //student already exist, update the exist data with the parameters received trough the form.
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
            else if(username.endsWith("@polito.it") && username.startsWith("d"))
            {
                user = AddGenericUser(username,psw);
                String id = username.split("@")[0];
                optTeacher = teachers.findByIdIgnoreCase(id);
                if(!optTeacher.isPresent())
                {
                    //insert new teacher
                    teacher = Teacher.builder()
                            .id(id)
                            .email(username)
                            .name(data.getName())
                            .lastName(data.getLastName())
                            .build();
                    teachers.save(teacher);
                }
            }
            else
            {
                throw new BadCredentialsException("Invalid username/password supplied");
            }

            notificationService.notifyRegistration(modelMapper.map(user, UserDTO.class));
            Map<Object, Object> model = new HashMap<>();
            model.put("username", user.getUsername());
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }
}
