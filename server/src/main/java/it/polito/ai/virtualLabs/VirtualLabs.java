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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    CommandLineRunner runner(UserRepository userRepository,
                             CourseRepository courseRepository,
                             PasswordEncoder passwordEncoder,
                             VmModelRepository vmModelRepository,
                             AssignmentRepository assignmentRepository,
                             TeamRepository teamRepository,
                             StudentRepository studentRepository,
                             TeamService teamService,
                             NotificationService notificationService,
                             TokenTeamRepository tokenTeamRepository,
                             VmConfigurationRepository vmConfigurationRepository,
                             PaperRepository paperRepository) {
        return new CommandLineRunner() {

            @Override
            public void run(String... args) throws Exception {
                //create default VmConfiguration if not exists
                if (vmConfigurationRepository.findAll().stream().noneMatch(vc -> vc.getVmModel() == null)) {
                    vmConfigurationRepository.save(VmConfiguration.builder()
                            .vmModel(null)
                            .maxVcpusPerVm(5)
                            .maxRamPerVm(500)
                            .maxDiskPerVm(500)
                            .maxRunningVms(2)
                            .maxVms(4)
                            .build());
                }

                generateMockData(courseRepository, vmModelRepository, teamRepository, userRepository, assignmentRepository, passwordEncoder, studentRepository, teamService, notificationService, tokenTeamRepository, paperRepository);

                System.out.println("Printing all courses:");
                courseRepository.findAll().forEach(v -> System.out.println(" - Course :" + v.toString()));
                System.out.println("Printing all users:");
                userRepository.findAll().forEach(v -> System.out.println(" - User :" + v.toString()));
                System.out.println("Printing all teams:");
                teamRepository.findAll().forEach(v -> System.out.println(" - Team :" + v.toString()));
                System.out.println("Printing all students:");
                studentRepository.findAll().forEach(student -> System.out.println(" - Student :" + student.toString()));
                System.out.println("Printing all assignments:");
                assignmentRepository.findAll().forEach(assignment -> System.out.println(" - Assignments :" + assignment.toString()));
                System.out.println("Printing all papers:");
                paperRepository.findAll().forEach(paper -> System.out.println(" - Paper :" + paper.toString()));

            }
        };
    }

    /* generates mock data (3 courses, admin, 2 students, team, teacher) */
    public void generateMockData(
            CourseRepository cr,
            VmModelRepository vmr,
            TeamRepository tr,
            UserRepository ur,
            AssignmentRepository ar,
            PasswordEncoder passwordEncoder,
            StudentRepository sr,
            TeamService teamService,
            NotificationService notificationService,
            TokenTeamRepository ttr,
            PaperRepository pr) {

        if (!ur.findAll().stream().anyMatch(u -> u.getUsername().equals("admin@polito.it"))) {
            try {

                /* Set time for the assignment releaseDate and expirtyDate */
                Calendar calendar = Calendar.getInstance();
                Timestamp releaseDate = new Timestamp(System.currentTimeMillis());
                calendar.setTimeInMillis(releaseDate.getTime());
                calendar.add(Calendar.DAY_OF_WEEK, 6);
                Timestamp expiryDate = new Timestamp(calendar.getTime().getTime());

                //Course: PDS
                Course newCourse = Course.builder()
                        .name("Programmazione di Sistema")
                        .acronym("PDS")
                        .enabled(true)
                        .assignments(new ArrayList<>())
                        .min(2)
                        .max(4)
                        .build();
                cr.save(newCourse);
                VmModel newVmModel = VmModel.builder()
                        .name("VmModelDefault-" + newCourse.getAcronym())
                        .image("ThisIsTheDefaultVmImage")
                        .course(newCourse)
                        .build();
                vmr.save(newVmModel);
                Paper paper1 = Paper.builder()
                        .status(null)
                        .vote(0)
                        .lastUpdateTime(releaseDate)
                        .build();
                Paper paper4 = Paper.builder()
                        .status(null)
                        .vote(0)
                        .lastUpdateTime(releaseDate)
                        .build();
                Paper paper5 = Paper.builder()
                        .status(null)
                        .vote(0)
                        .lastUpdateTime(releaseDate)
                        .build();
                Assignment assignment1 = Assignment.builder()
                        .releaseDate(releaseDate)
                        .expiryDate(expiryDate)
                        .papers(new ArrayList<>())
                        .content("Laboratorio 1")
                        .build();
                Assignment assignment4 = Assignment.builder()
                        .releaseDate(releaseDate)
                        .expiryDate(expiryDate)
                        .papers(new ArrayList<>())
                        .content("Laboratorio 2")
                        .build();
                Assignment assignment5 = Assignment.builder()
                        .releaseDate(releaseDate)
                        .expiryDate(expiryDate)
                        .papers(new ArrayList<>())
                        .content("Laboratorio 3")
                        .build();
                paper1.setAssignment(assignment1);
                paper4.setAssignment(assignment1);
                paper5.setAssignment(assignment1);
                assignment1.setCourse(newCourse);
                assignment4.setCourse(newCourse);
                assignment5.setCourse(newCourse);
                ar.save(assignment1);
                ar.save(assignment4);
                ar.save(assignment5);

                //Course: ML
                newCourse = Course.builder()
                        .name("Machine Learning")
                        .acronym("ML")
                        .enabled(true)
                        .assignments(new ArrayList<>())
                        .min(3)
                        .max(6)
                        .build();
                cr.save(newCourse);
                newVmModel = VmModel.builder()
                        .name("VmModelDefault-" + newCourse.getAcronym())
                        .image("ThisIsTheDefaultVmImage")
                        .course(newCourse)
                        .build();
                vmr.save(newVmModel);
                Paper paper2 = Paper.builder()
                        .status(null)
                        .vote(0)
                        .lastUpdateTime(releaseDate)
                        .build();
                Assignment assignment2 = Assignment.builder()
                        .releaseDate(releaseDate)
                        .expiryDate(expiryDate)
                        .papers(new ArrayList<>())
                        .content("Laboratorio 1")
                        .build();
                paper2.setAssignment(assignment2);
                assignment2.setCourse(newCourse);
                ar.save(assignment2);

                //Course: AI
                newCourse = Course.builder()
                        .name("Applicazioni Internet")
                        .acronym("AI")
                        .enabled(false)
                        .assignments(new ArrayList<>())
                        .min(5)
                        .max(10)
                        .build();
                cr.save(newCourse);
                newVmModel = VmModel.builder()
                        .name("VmModelDefault-" + newCourse.getAcronym())
                        .image("ThisIsTheDefaultVmImage")
                        .course(newCourse)
                        .build();
                vmr.save(newVmModel);
                Paper paper3 = Paper.builder()
                        .status(null)
                        .vote(0)
                        .lastUpdateTime(releaseDate)
                        .build();
                Assignment assignment3 = Assignment.builder()
                        .releaseDate(releaseDate)
                        .expiryDate(expiryDate)
                        .papers(new ArrayList<>())
                        .content("Laboratorio 1")
                        .build();
                paper3.setAssignment(assignment3);
                assignment3.setCourse(newCourse);
                ar.save(assignment3);

                //User-Admin
                ur.save(User.builder()
                        .id("s000000")
                        .username("admin@polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT", "ROLE_PROF", "ROLE_ADMIN"))
                        .build()
                );
                //User-teacher
                ur.save(User.builder()
                        .id("s654321")
                        .username("teacher@polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_PROF"))
                        .build()
                );
                //set admin credentials to call protected functions
                SecurityContext ctx = SecurityContextHolder.createEmptyContext();
                SecurityContextHolder.setContext(ctx);
                ctx.setAuthentication(new UsernamePasswordAuthenticationToken("admin@polito.it", "pwd"));
                //User-Student: s123456 (Mario Rossi)
                ur.save(User.builder()
                        .id("s123456")
                        .username("s123456@studenti.polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT"))
                        .build()
                );
                Student newStudent = Student.builder()
                        .id("s123456")
                        .email("s123456@studenti.polito.it")
                        .lastName("Rossi")
                        .name("Mario")
                        .papers(new ArrayList<>())
                        .build();
                paper1.setStudent(newStudent);
                paper5.setStudent(newStudent);
                sr.save(newStudent);
                //User-Student: s234567 (Giacomo Bianchi)
                ur.save(User.builder()
                        .id("s234567")
                        .username("s234567@studenti.polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT"))
                        .build()
                );
                sr.save(Student.builder()
                        .id("s234567")
                        .email("s234567@studenti.polito.it")
                        .lastName("Bianchi")
                        .name("Giacomo")
                        .build());
                //enroll studenti in pds
                teamService.enrollAll(new ArrayList<String>(Arrays.asList("s123456", "s234567")), "Programmazione di Sistema");
                //crea team
                TeamDTO teamDTO = teamService.proposeTeam("Programmazione di Sistema", "Team1", new ArrayList<String>(Arrays.asList("s234567")), "s123456", 1);
                //invia e accetta inviti team
                notificationService.notifyTeam(teamDTO, new ArrayList<String>(Arrays.asList("s123456", "s234567")), 1);
                notificationService.confirm(ttr.findAll().stream().filter(tt -> tt.getTeam().getName().equals("Team1")).findFirst().get().getId()); // che brutto
                notificationService.confirm(ttr.findAll().stream().filter(tt -> tt.getTeam().getName().equals("Team1")).findFirst().get().getId());
                //User-Student: s345678 (Dario Verdi)
                ur.save(User.builder()
                        .id("s345678")
                        .username("s345678@studenti.polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT"))
                        .build()
                );
                newStudent = Student.builder()
                        .id("s345678")
                        .email("s345678@studenti.polito.it")
                        .lastName("Dario")
                        .name("Verdi")
                        .papers(new ArrayList<>())
                        .build();
                paper2.setStudent(newStudent);
                paper3.setStudent(newStudent);
                paper4.setStudent(newStudent);
                sr.save(newStudent);
                pr.save(paper1);
                pr.save(paper2);
                pr.save(paper3);
                pr.save(paper4);
                pr.save(paper5);
            } catch (Exception e) {
                System.out.println("Exception insert mock data: " + e.getMessage());
            } finally {
                SecurityContextHolder.clearContext();
            }
        }
    }

}
