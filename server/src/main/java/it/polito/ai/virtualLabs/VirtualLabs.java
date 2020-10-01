package it.polito.ai.virtualLabs;
import it.polito.ai.virtualLabs.entities.User;
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
    CommandLineRunner runner(UserRepository users,PasswordEncoder passwordEncoder ) {
        return new CommandLineRunner() {


            @Override
            public void run(String... args) throws Exception {
                try{
                    users.save(User.builder()
                            .username("student")
                            .password(passwordEncoder.encode("pwd"))
                            .roles(Arrays.asList( "ROLE_PROF"))
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
                    );
                }catch (Exception e){
                    System.out.println("Exception insert user: "+e.getMessage().toString());
                }

                System.out.println("printing all users...");
                users.findAll().forEach(v ->  System.out.println(" User :" + v.toString()));



            }
        };
    }


}
