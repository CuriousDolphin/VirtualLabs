package it.polito.ai.lab3;

import it.polito.ai.lab3.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class Lab2Application {
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }
    @Bean
    CommandLineRunner runner(UserRepository users,PasswordEncoder passwordEncoder ) {
        return new CommandLineRunner() {


            @Override
            public void run(String... args) throws Exception {
                /*users.save(User.builder()
                        .username("student")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList( "ROLE_STUDENT"))
                        .build()
                );
                users.save(User.builder()
                        .username("prof")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList( "ROLE_PROF"))
                        .build()
                );

                users.save(User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT","ROLE_PROF", "ROLE_ADMIN"))
                        .build()
                );*/
                System.out.println("printing all users...");
                users.findAll().forEach(v ->  System.out.println(" User :" + v.toString()));



            }
        };
    }


}
