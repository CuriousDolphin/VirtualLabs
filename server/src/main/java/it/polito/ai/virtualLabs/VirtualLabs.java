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
                             PaperRepository paperRepository,
                             PaperSnapshotRepository paperSnapshotRepository) {
        return new CommandLineRunner() {

            @Override
            public void run(String... args) throws Exception {

                //create default VmConfiguration if not exists
                if (vmConfigurationRepository.findAll().stream().noneMatch(vc -> vc.getVmModel() == null)) {
                    vmConfigurationRepository.save(VmConfiguration.builder()
                            .vmModel(null)
                            .maxVcpusPerVm(5*6)
                            .maxRamPerVm(8*6)
                            .maxDiskPerVm(500*6)
                            .maxRunningVms(3)
                            .maxVms(6)
                            .build());
                }

                generateMockData(courseRepository, vmModelRepository, teamRepository, userRepository, assignmentRepository, passwordEncoder, studentRepository, teamService, notificationService, tokenTeamRepository, paperRepository, paperSnapshotRepository);

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
                System.out.println("Printing all paperSnapshots:");
                paperSnapshotRepository.findAll().forEach(paperSnapshot -> System.out.println(" - PaperSnapshot :" + paperSnapshot.toString()));
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
            PaperRepository pr,
            PaperSnapshotRepository psr) {

        if (!ur.findAll().stream().anyMatch(u -> u.getUsername().equals("admin@polito.it"))) {
            try {

                /* Set time for the assignment releaseDate and expirtyDate */
                Calendar calendar = Calendar.getInstance();
                Timestamp releaseDate = new Timestamp(System.currentTimeMillis());
                calendar.setTimeInMillis(releaseDate.getTime());
                calendar.add(Calendar.DAY_OF_WEEK, 6);
                Timestamp expiryDate = new Timestamp(calendar.getTime().getTime());

                //Course: PDS
                Course course1 = Course.builder()
                        .name("Programmazione di Sistema")
                        .acronym("PDS")
                        .enabled(true)
                        .assignments(new ArrayList<>())
                        .min(2)
                        .max(4)
                        .build();

                VmModel VmModel1 = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(course1)
                        .build();
                Paper paper1 = Paper.builder()
                        .status(null)
                        .vote(0)
                        .lastUpdateTime(releaseDate)
                        .paperSnapshots(new ArrayList<>())
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
                PaperSnapshot paperSnapshot1 = PaperSnapshot.builder()
                        .content("Soluzione")
                        .submissionDate(expiryDate)
                        .build();
                PaperSnapshot paperSnapshot2 = PaperSnapshot.builder()
                        .content("Soluzione")
                        .submissionDate(expiryDate)
                        .build();
                PaperSnapshot paperSnapshot3 = PaperSnapshot.builder()
                        .content("Soluzione")
                        .submissionDate(expiryDate)
                        .build();
                paperSnapshot1.setPaper(paper1);
                paperSnapshot2.setPaper(paper1);
                paperSnapshot3.setPaper(paper1);
                assignment1.setCourse(course1);
                assignment4.setCourse(course1);
                assignment5.setCourse(course1);

                //Course: ML
                Course course2 = Course.builder()
                        .name("Machine Learning")
                        .acronym("ML")
                        .enabled(true)
                        .assignments(new ArrayList<>())
                        .min(3)
                        .max(6)
                        .build();
                VmModel VmModel2 = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(course2)
                        .build();
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
                assignment2.setCourse(course2);

                //Course: AI
                Course course3 = Course.builder()
                        .name("Applicazioni Internet")
                        .acronym("AI")
                        .enabled(false)
                        .assignments(new ArrayList<>())
                        .min(5)
                        .max(10)
                        .build();
                VmModel VmModel3 = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(course3)
                        .build();
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
                assignment3.setCourse(course3);

                //User-Admin
                ur.save(User.builder()
                        .id("s000000")
                        .enabled(true)
                        .username("admin@polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT", "ROLE_PROF", "ROLE_ADMIN"))
                        .build()
                );

                //User-teacher
                ur.save(User.builder()
                        .id("s654321")
                        .enabled(true)
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
                paper1.setStudent(student1);
                paper5.setStudent(student1);

                //User-Student: s234567 (Giacomo Bianchi)
                ur.save(User.builder()
                        .id("s234567")
                        .enabled(true)
                        .username("s234567@studenti.polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT"))
                        .build()
                );
                 Student student3 = Student.builder()
                        .id("s234567")
                        .email("s234567@studenti.polito.it")
                        .lastName("Bianchi")
                        .name("Giacomo")
                        .build();

                //enroll studenti in pds
                cr.save(course1);
                sr.save(student1);
                sr.save(student3);
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
                        .enabled(true)
                        .username("s345678@studenti.polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT"))
                        .build()
                );
                Student student2 = Student.builder()
                        .id("s345678")
                        .email("s345678@studenti.polito.it")
                        .lastName("Dario")
                        .name("Verdi")
                        .papers(new ArrayList<>())
                        .build();
                paper2.setStudent(student2);
                paper3.setStudent(student2);
                paper4.setStudent(student2);

                cr.save(course2);
                cr.save(course3);
                vmr.save(VmModel1);
                vmr.save(VmModel2);
                vmr.save(VmModel3);
                sr.save(student2);
                ar.save(assignment1);
                ar.save(assignment2);
                ar.save(assignment3);
                ar.save(assignment4);
                ar.save(assignment5);
                pr.save(paper1);
                pr.save(paper2);
                pr.save(paper3);
                pr.save(paper4);
                pr.save(paper5);
                psr.save(paperSnapshot1);
                psr.save(paperSnapshot2);
                psr.save(paperSnapshot3);

            }
            catch (Exception e) {
                System.out.println("Exception insert mock data: " + e.getMessage());
            } finally {
                SecurityContextHolder.clearContext();
            }
        }
    }

}
