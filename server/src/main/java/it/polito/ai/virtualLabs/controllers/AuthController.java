package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.repositories.CourseRepository;
import it.polito.ai.virtualLabs.repositories.StudentRepository;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import it.polito.ai.virtualLabs.security.AuthenticationRequest;
import it.polito.ai.virtualLabs.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

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
    StudentRepository studentRepository;

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody AuthenticationRequest data) {
        try {
            String email = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, data.getPassword()));

            String id ="";
            Optional<Student> s=studentRepository.findByEmailIgnoreCase(email);
            if(s.isPresent())
                id=s.get().getId();

            String token = jwtTokenProvider.createToken(
                    email,
                    this.users.findByUsername(email)
                            .orElseThrow(() -> new UsernameNotFoundException("Username " + email + "not found")).getRoles(),id);
            Map<Object, Object> model = new HashMap<>();
            model.put("username", email);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }
}
