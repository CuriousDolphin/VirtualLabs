package it.polito.ai.virtualLabs;

import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.repositories.*;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

@Log(topic = "VirtualLabs")
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
    CommandLineRunner runner(UserRepository userRepository,
                             CourseRepository courseRepository,
                             PasswordEncoder passwordEncoder,
                             VmModelRepository vmModelRepository,
                             AssignmentRepository assignmentRepository) {
        return new CommandLineRunner() {

            @Override
            public void run(String... args) throws Exception {

                //generateMockCourses(courseRepository, vmModelRepository, assignmentRepository);

                //generateMockUsers(userRepository, passwordEncoder);

                System.out.println("\nPrinting all courses:");
                courseRepository.findAll().forEach(v -> System.out.println(" Course :" + v.toString()));
                System.out.println("\nPrinting all users:");
                userRepository.findAll().forEach(v -> System.out.println(" User :" + v.toString()));
                System.out.println("\nPrint all assignment");
                assignmentRepository.findAll().forEach(assignment -> System.out.println(" Assignment :" + assignment.toString()));

            }
        };
    }

    /* generates (if not already exist) mock courses and relatives vmmodels */
    public void generateMockCourses(CourseRepository cr, VmModelRepository vmr, AssignmentRepository ar) {

        /* Insert courses */
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
        } catch (Exception e) {
            System.out.println("Exception insert courses: " + e.getMessage());
        }

        Course course1 = cr.findByNameIgnoreCase("Programmazione Di Sistema").get();
        Course course2 = cr.findByNameIgnoreCase("Machine Learning").get();
        Course course3 = cr.findByNameIgnoreCase("Applicazioni Internet").get();

        /* Insert vms */
        try {
            vmr.save(VmModel.builder()
                    .name("VmModelDefault-" + course1.getAcronym())
                    .image("ThisIsTheDefaultVmImage")
                    .course(course1)
                    .build());
            vmr.save(VmModel.builder()
                    .name("VmModelDefault-" + course2.getAcronym())
                    .image("ThisIsTheDefaultVmImage")
                    .course(course2)
                    .build());
            vmr.save(VmModel.builder()
                    .name("VmModelDefault-" + course3.getAcronym())
                    .image("ThisIsTheDefaultVmImage")
                    .course(course3)
                    .build());
        } catch (Exception e) {
            System.out.println("Exception insert vms: " + e.getMessage());
        }

        /* Set time for the assignment releaseDate and expirtyDate */
        Calendar calendar = Calendar.getInstance();
        Timestamp releaseDate = new Timestamp(System.currentTimeMillis());
        calendar.setTimeInMillis(releaseDate.getTime());
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Timestamp expiryDate = new Timestamp(calendar.getTime().getTime());

        /* Insert assignment */
        try {

            ar.save(Assignment.builder()
                    .releaseDate(releaseDate)
                    .expiryDate(expiryDate)
                    .content("Laboratorio 1")
                    .course(course1)
                    .build());

            ar.save(Assignment.builder()
                    .releaseDate(releaseDate)
                    .expiryDate(expiryDate)
                    .content("Laboratorio 1")
                    .course(course2)
                    .build());


            ar.save(Assignment.builder()
                    .releaseDate(releaseDate)
                    .expiryDate(expiryDate)
                    .content("Laboratorio 1")
                    .course(course3)
                    .build());

        } catch (Exception e) {
            System.out.println("Exception insert vms: " + e.getMessage());
        }

        
    }

    /* generates (if not already exist) mock student, teacher, admin */
    public void generateMockUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        try {
            userRepository.save(User.builder()
                    .id("sStudent")
                    .username("student@studenti.polito.it")
                    .password(passwordEncoder.encode("pwd"))
                    .roles(Arrays.asList("ROLE_STUDENT"))
                    .build()
            );
            userRepository.save(User.builder()
                    .id("sTeacher")
                    .username("teacher@polito.it")
                    .password(passwordEncoder.encode("pwd"))
                    .roles(Arrays.asList("ROLE_PROF"))
                    .build()
            );

            userRepository.save(User.builder()
                    .id("sADMIN")
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
