package it.polito.ai.virtualLabs;

import it.polito.ai.virtualLabs.entities.Course;
import it.polito.ai.virtualLabs.entities.User;
import it.polito.ai.virtualLabs.entities.VmModel;
import it.polito.ai.virtualLabs.repositories.CourseRepository;
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

                generateMockCourses(3, courseRepository, vmModelRepository);

                generateMockUsers(userRepository, passwordEncoder);

                System.out.println("printing all users...");
                userRepository.findAll().forEach(v -> System.out.println(" User :" + v.toString()));
                courseRepository.findAll().forEach(v -> System.out.println(" Course :" + v.toString()));

            }
        };
    }

    /* generates (if not already exists) n mock courses and relatives vmmodels */
    public void generateMockCourses(int n, CourseRepository cr, VmModelRepository vmr) {
        for (int i = 0; i < n; i++) {
            try {
                Course newCourse = Course.builder()
                        .name("Course" + i)
                        .acronym("c" + i)
                        .enabled(true)
                        .min(10)
                        .max(100)
                        .build();
                cr.save(newCourse);
            } catch (Exception e) {
                System.out.println("Exception insert course: " + e.getMessage());
            }
            try {
                Course course = cr.findByNameIgnoreCase("Course" + i).get();
                VmModel newVmModel = VmModel.builder()
                        .name("VmModelDefault-" + course.getName())
                        .image("ThisIsTheDefaultVmImage")
                        .course(course)
                        .build();
                vmr.save(newVmModel);
            } catch (Exception e) {
                System.out.println("Exception insert vmmodel: " + e.getMessage());
            }
        }
    }

    /* generates (if not already exists) mock student, teacher, admin */
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
