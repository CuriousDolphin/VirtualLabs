package it.polito.ai.virtualLabs;
import it.polito.ai.virtualLabs.entities.Course;
import it.polito.ai.virtualLabs.entities.User;
import it.polito.ai.virtualLabs.repositories.CourseRepository;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class VirtualLabs {
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(VirtualLabs.class, args);
    }
    @Bean
    CommandLineRunner runner(UserRepository users, CourseRepository courses, PasswordEncoder passwordEncoder ) {
        return new CommandLineRunner() {


            @Override
            public void run(String... args) throws Exception {
                try{
                    courses.save(Course.builder()
                            .name("Programmazione di sistema")
                            .acronym("pds")
                            .enabled(true)
                            .min(10)
                            .max(100)
                            .build()
                    );
                    courses.save(Course.builder()
                            .name("Machine Learning")
                            .acronym("ML")
                            .enabled(true)
                            .min(10)
                            .max(100)
                            .build()
                    );
                    courses.save(Course.builder()
                            .name("Applicazioni Internet")
                            .acronym("AI")
                            .enabled(true)
                            .min(10)
                            .max(100)
                            .build()
                    );
                    users.save(User.builder()
                            .username("student@studenti.polito.it")
                            .password(passwordEncoder.encode("pwd"))
                            .roles(Arrays.asList( "ROLE_STUDENT"))
                            .enabled(true)
                            .build()
                    );
                    users.save(User.builder()
                            .username("teacher@polito.it")
                            .password(passwordEncoder.encode("pwd"))
                            .roles(Arrays.asList( "ROLE_PROF"))
                            .enabled(true)
                            .build()
                    );

                    users.save(User.builder()
                            .username("admin@polito.it")
                            .password(passwordEncoder.encode("pwd"))
                            .roles(Arrays.asList("ROLE_STUDENT","ROLE_PROF", "ROLE_ADMIN"))
                            .enabled(true)
                            .build()
                    );

                }catch (Exception e){
                    System.out.println("Exception insert user: "+e.getMessage().toString());
                }

                System.out.println("printing all users...");
                users.findAll().forEach(v ->  System.out.println(" User :" + v.toString()));
                courses.findAll().forEach(v ->  System.out.println(" Course :" + v.toString()));



            }
        };
    }


}
