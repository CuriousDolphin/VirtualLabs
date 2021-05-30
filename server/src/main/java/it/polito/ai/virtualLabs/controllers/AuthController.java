package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.UserProposal;
import it.polito.ai.virtualLabs.dtos.UserDTO;
import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.entities.Teacher;
import it.polito.ai.virtualLabs.entities.User;
import it.polito.ai.virtualLabs.exceptions.ImageException;
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

import javax.imageio.ImageIO;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

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
            Optional<User> u = this.users.findByUsername(email);
            if(!u.isPresent())
                throw new UsernameNotFoundException("Username " + email + "not found");


            String photo = u.get().getPhoto() != null && u.get().getPhoto().toString() != "" ?
                    "data:image/png;base64," + Base64.getMimeEncoder().encodeToString(u.get().getPhoto()) :
                    "";
            String token = jwtTokenProvider.createToken(
                    email,
                    u.get().getRoles(), id, photo);
            Map<Object, Object> model = new HashMap<>();
            model.put("username", email);
            model.put("token", token);
            //model.put("userId", id);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    public byte[] ResizeImg(byte[] fileData, int width, int height) {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        try {
            BufferedImage img = ImageIO.read(in);
            if(height == 0) {
                height = (width * img.getHeight())/ img.getWidth();
            }
            if(width == 0) {
                width = (height * img.getWidth())/ img.getHeight();
            }
            Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            ImageIO.write(imageBuff, "jpg", buffer);

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new ImageException();
        }
    }

    public User AddGenericUser(String username, String psw, byte[] photo)
    {
        if(users.findByUsername(username.toLowerCase()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists!");
        User u;
        if(photo != null)
        {
            photo = ResizeImg(photo,45,45);
            u = User.builder().id(username.toLowerCase().split("@")[0])
                    .username(username.toLowerCase())
                    .password(passwordEncoder.encode(psw))
                    .enabled(false)
                    .photo(photo)
                    .roles(username.toLowerCase().startsWith("d") ? Arrays.asList( "ROLE_PROF") : Arrays.asList( "ROLE_STUDENT"))
                    .build();
        }
        else
        {
            u = User.builder().id(username.toLowerCase().split("@")[0])
                    .username(username.toLowerCase())
                    .password(passwordEncoder.encode(psw))
                    .enabled(false)
                    .roles(username.toLowerCase().startsWith("d") ? Arrays.asList( "ROLE_PROF") : Arrays.asList( "ROLE_STUDENT"))
                    .build();
        }
        //System.out.println("aaaaaaaa" + photo.toString());
        /*User u = User.builder().id(username.toLowerCase().split("@")[0])
                .username(username.toLowerCase())
                .password(passwordEncoder.encode(psw))
                .enabled(false)
                .photo(photo)
                .roles(username.toLowerCase().startsWith("d") ? Arrays.asList( "ROLE_PROF") : Arrays.asList( "ROLE_STUDENT"))
                .build();*/

        users.save(u);
        u = users.findByUsername(username.toLowerCase()).get();
        return u;
    }

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody @Valid UserProposal data) {
        try {
            String username = data.getUsername().toLowerCase();
            String psw = data.getPassword();
            User user;
            Optional<Student> optStudent;
            Student student;
            Optional<Teacher> optTeacher;
            Teacher teacher;
            byte[] photo = null;
            if(data.getPhoto() != null && data.getPhoto() != "")
            {
                photo = Base64.getMimeDecoder().decode(data.getPhoto().split(",")[1]);
            }


            if(!(data.getPassword().equals(data.getConfirmPassword())))
            {
                throw new BadCredentialsException("Passwords not match");
            }
            if(username.endsWith("@studenti.polito.it") && username.startsWith("s"))
            {
                user = AddGenericUser(username,psw,photo);
                String id = username.split("@")[0].toLowerCase();
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
                user = AddGenericUser(username,psw,photo);
                String id = username.split("@")[0].toLowerCase();
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
