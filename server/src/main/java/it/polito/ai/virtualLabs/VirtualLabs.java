package it.polito.ai.virtualLabs;

import it.polito.ai.virtualLabs.entities.Course;
import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.entities.User;
import it.polito.ai.virtualLabs.entities.VmModel;
import it.polito.ai.virtualLabs.repositories.CourseRepository;
import it.polito.ai.virtualLabs.repositories.StudentRepository;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import it.polito.ai.virtualLabs.repositories.VmModelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

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
    CommandLineRunner runner(UserRepository userRepository, CourseRepository courseRepository, PasswordEncoder passwordEncoder, VmModelRepository vmModelRepository) {
        return new CommandLineRunner() {

            @Override
            public void run(String... args) throws Exception {

                generateMockCourses(courseRepository, vmModelRepository);

                generateMockUsers(userRepository, passwordEncoder);

                System.out.println("printing all users...");

            }
        };
    }

    /* generates (if not already exist) mock courses and relatives vmmodels */
    public void generateMockCourses(CourseRepository cr, VmModelRepository vmr) {
        try {
            cr.save(Course.builder()
                    .name("Programmazione di Sistema")
                    .acronym("PDS")
                    .enabled(true)
                    .min(2)
                    .max(4)
                    .build());
            cr.save(Course.builder()
                    .name("Machine Learning")
                    .acronym("ML")
                    .enabled(true)
                    .min(3)
                    .max(6)
                    .build());
            cr.save(Course.builder()
                    .name("Applicazioni Internet")
                    .acronym("AI")
                    .enabled(false)
                    .min(5)
                    .max(10)
                    .build());

            Course course = cr.findByNameIgnoreCase("Programmazione Di Sistema").get();
            vmr.save(VmModel.builder()
                    .name("VmModelDefault-" + course.getAcronym())
                    .image("ThisIsTheDefaultVmImage")
                    .course(course)
                    .build());
            course = cr.findByNameIgnoreCase("Machine Learning").get();
            vmr.save(VmModel.builder()
                    .name("VmModelDefault-" + course.getAcronym())
                    .image("ThisIsTheDefaultVmImage")
                    .course(course)
                    .build());
            course = cr.findByNameIgnoreCase("Applicazioni Internet").get();
            vmr.save(VmModel.builder()
                    .name("VmModelDefault-" + course.getAcronym())
                    .image("ThisIsTheDefaultVmImage")
                    .course(course)
                    .build());
        } catch (Exception e) {
            System.out.println("Exception insert courses/vmmodels: " + e.getMessage());
        }
    }

    /* generates (if not already exist) mock student, teacher, admin */
    public void generateMockUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        try {
            userRepository.save(User.builder()
                    .username("student@studenti.polito.it")
                    .password(passwordEncoder.encode("pwd"))
                    .roles(Arrays.asList("ROLE_STUDENT"))
                    .build()
            );
            userRepository.save(User.builder()
                    .username("teacher@polito.it")
                    .password(passwordEncoder.encode("pwd"))
                    .roles(Arrays.asList("ROLE_PROF"))
                    .build()
            );

            userRepository.save(User.builder()
                    .username("admin@polito.it")
                    .password(passwordEncoder.encode("pwd"))
                    .roles(Arrays.asList("ROLE_STUDENT", "ROLE_PROF", "ROLE_ADMIN"))
                    .build()
            );
        } catch (Exception e) {
            System.out.println("Exception insert user: " + e.getMessage());
        }
    }

}
