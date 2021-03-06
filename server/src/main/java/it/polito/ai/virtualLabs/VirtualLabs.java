package it.polito.ai.virtualLabs;

import lombok.extern.java.Log;
import it.polito.ai.virtualLabs.dtos.CourseDTO;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.repositories.*;
import it.polito.ai.virtualLabs.services.NotificationService;
import it.polito.ai.virtualLabs.services.TeamService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

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
    CommandLineRunner runner(
            VmInstanceRepository vmInstanceRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            PasswordEncoder passwordEncoder,
            VmModelRepository vmModelRepository,
            StudentRepository studentRepository,
            TeamService teamService,
            NotificationService notificationService,
            TokenTeamRepository tokenTeamRepository,
            TeacherRepository teacherRepository,
            AssignmentRepository assignmentRepository,
            PaperRepository paperRepository,
            PaperSnapshotRepository paperSnapshotRepository) {
        return new CommandLineRunner() {

            @Override
            public void run(String... args) throws Exception {

                //insert User-Admin if not exists
                if(userRepository.findByUsername("admin").isEmpty()) {
                    userRepository.save(User.builder()
                            .id("admin")
                            .username("admin@polito.it")
                            .enabled(true)
                            .password(passwordEncoder.encode("pwd"))
                            .roles(Arrays.asList("ROLE_STUDENT", "ROLE_PROF", "ROLE_ADMIN"))
                            .build()
                    );
                    teacherRepository.save(Teacher.builder()
                            .id("admin")
                            .email("admin@polito.it")
                            .lastName("admin")
                            .name("admin")
                            .courses(courseRepository.findAll())
                            .build());
                }

                /*generateMockData(
                        teamRepository,
                        vmInstanceRepository,
                        courseRepository,
                        vmModelRepository,
                        userRepository,
                        passwordEncoder,
                        studentRepository,
                        teamService,
                        notificationService,
                        tokenTeamRepository,
                        teacherRepository);
                */
                System.out.println("Printing all users:");
                userRepository.findAll().forEach(v ->  System.out.println(" - User: " + v.toString()));
                System.out.println("Printing all courses:");
                courseRepository.findAll().forEach(v ->  System.out.println(" - Course: " + v.toString()));
                System.out.println("Printing all teams:");
                teamRepository.findAll().forEach(v ->  System.out.println(" - Team: " + v.toString()));

            }
        };
    }

    /* generates mock data (3 courses, admin, 2 students, team, teacher) */
    public void generateMockData(
            TeamRepository tr,
            VmInstanceRepository ir,
            CourseRepository cr,
            VmModelRepository vmr,
            UserRepository ur,
            PasswordEncoder passwordEncoder,
            StudentRepository sr,
            TeamService teamService,
            NotificationService notificationService,
            TokenTeamRepository ttr,
            TeacherRepository tcr) {
        if(cr.findByNameIgnoreCase("Programmazione di Sistema").isEmpty()) {
            try {

                //Course: PDS
                Course newCourse1 = Course.builder()
                        .name("Programmazione di Sistema")
                        .acronym("PDS")
                        .enabled(true)
                        .assignments(new ArrayList<>())
                        .min(2)
                        .max(4)
                        .build();
                cr.save(newCourse1);
                VmModel newVmModel = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(newCourse1)
                        .maxVms(6)
                        .maxRunningVms(3)
                        .maxVcpus(6*5)
                        .maxRam(6*8)
                        .maxDisk(6*500)
                        .build();
                vmr.save(newVmModel);

                //Course: ML
                Course newCourse2 = Course.builder()
                        .name("Machine Learning")
                        .acronym("ML")
                        .enabled(true)
                        .assignments(new ArrayList<>())
                        .min(3)
                        .max(6)
                        .build();
                cr.save(newCourse2);
                newVmModel = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(newCourse2)
                        .maxVms(6)
                        .maxRunningVms(3)
                        .maxVcpus(6*5)
                        .maxRam(6*8)
                        .maxDisk(6*500)
                        .build();
                vmr.save(newVmModel);

                //Course: AI
                Course newCourse3 = Course.builder()
                        .name("Applicazioni Internet")
                        .acronym("AI")
                        .enabled(false)
                        .assignments(new ArrayList<>())
                        .min(5)
                        .max(10)
                        .build();
                cr.save(newCourse3);
                newVmModel = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(newCourse3)
                        .maxVms(6)
                        .maxRunningVms(3)
                        .maxVcpus(6*5)
                        .maxRam(6*8)
                        .maxDisk(6*500)
                        .build();
                vmr.save(newVmModel);
                //set admin as teacher for all new courses
                if(tcr.findByIdIgnoreCase("admin").isPresent())
                    tcr.deleteById("admin");
                tcr.save(Teacher.builder()
                        .id("admin")
                        .email("admin@polito.it")
                        .lastName("admin")
                        .name("admin")
                        .courses(cr.findAll())
                        .build());
                //User-teacher
                ur.save(User.builder()
                        .id("teacher")
                        .username("teacher@polito.it")
                        .enabled(true)
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_PROF"))
                        .build()
                );
                tcr.save(Teacher.builder()
                        .id("teacher")
                        .email("teacher@polito.it")
                        .lastName("DePaperoni")
                        .name("Paperon")
                        .courses(Arrays.asList(newCourse1, newCourse2, newCourse3))
                        .build());
                //set admin credentials to call protected functions
                SecurityContext ctx = SecurityContextHolder.createEmptyContext();
                SecurityContextHolder.setContext(ctx);
                ctx.setAuthentication(new UsernamePasswordAuthenticationToken("admin@polito.it", "pwd"));
                //User-Student: s123456 (Mario Rossi)
                ur.save(User.builder()
                        .id("s123456")
                        .enabled(true)
                        .username("s123456@studenti.polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT"))
                        .build()
                );

                Student student1 = Student.builder()
                        .id("s123456")
                        .email("s123456@studenti.polito.it")
                        .lastName("Rossi")
                        .name("Mario")
                        .papers(new ArrayList<>())
                        .build();

                //User-Student: s234567 (Giacomo Bianchi)
                ur.save(User.builder()
                        .id("s234567")
                        .enabled(true)
                        .username("s234567@studenti.polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT"))
                        .enabled(true)
                        .build()
                );
                 Student student3 = Student.builder()
                        .id("s234567")
                        .email("s234567@studenti.polito.it")
                        .lastName("Bianchi")
                        .name("Giacomo")
                        .build();
                //enroll studenti in pds
                cr.save(newCourse1);
                sr.save(student1);
                sr.save(student3);
                teamService.enrollAll(new ArrayList<String>(Arrays.asList("s123456", "s234567")), "Programmazione di Sistema");
                //crea team
                TeamDTO teamDTO = teamService.proposeTeam("Programmazione di Sistema", "TheDreamTeam", new ArrayList<String>(Arrays.asList("s234567")), "s123456", 1);
                //invia e accetta inviti team
                notificationService.notifyTeam(teamDTO, new ArrayList<String>(Arrays.asList("s123456", "s234567")), 1);
                notificationService.confirm(ttr.findAll().stream().filter(tt -> tt.getTeam().getName().equals("TheDreamTeam")).findFirst().get().getId()); // che brutto
                notificationService.confirm(ttr.findAll().stream().filter(tt -> tt.getTeam().getName().equals("TheDreamTeam")).findFirst().get().getId());
                //User-Student: s345678 (Dario Verdi)
                ur.save(User.builder()
                        .id("s345678")
                        .enabled(true)
                        .username("s345678@studenti.polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT"))
                        .enabled(true)
                        .build()
                );
                Student student2 = Student.builder()
                        .id("s345678")
                        .email("s345678@studenti.polito.it")
                        .lastName("Dario")
                        .name("Verdi")
                        .papers(new ArrayList<>())
                        .build();
                sr.save(student2);
                //insert VmInstance
                ir.save(VmInstance.builder()
                        .team(tr.getByName("TheDreamTeam"))
                        .vmModel(vmr.getByCourse(tr.getByName("TheDreamTeam").getCourse()))
                        .state(1)
                        .countVcpus(5)
                        .countRam(8)
                        .countDisks(500)
                        .owner(null)
                        .creator(tr.getByName("TheDreamTeam").getOwner().getId())
                        .image(vmr.getByCourse(tr.getByName("TheDreamTeam").getCourse()).getImage())
                        .build());
            } catch (Exception e) {
                System.out.println("Exception insert mock data: " + e.getMessage());
            } finally {
                SecurityContextHolder.clearContext();
            }
        }
    }

}
