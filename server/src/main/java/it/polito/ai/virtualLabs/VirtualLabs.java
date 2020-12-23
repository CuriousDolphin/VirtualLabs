package it.polito.ai.virtualLabs;

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

import java.util.ArrayList;
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
    CommandLineRunner runner(ModelMapper modelMapper, TeamRepository teamRepository, UserRepository userRepository, CourseRepository courseRepository, PasswordEncoder passwordEncoder, VmModelRepository vmModelRepository, StudentRepository studentRepository, TeamService teamService, NotificationService notificationService, TokenTeamRepository tokenTeamRepository, TeacherRepository teacherRepository) {
        return new CommandLineRunner() {

            @Override
            public void run(String... args) throws Exception {

                //create default VmConfiguration if not exists

                generateMockData(courseRepository, vmModelRepository, teamRepository, userRepository, passwordEncoder, studentRepository, teamService, notificationService, tokenTeamRepository, teacherRepository);

                System.out.println("Printing all courses:");
                courseRepository.findAll().forEach(v ->  System.out.println(" - Course :" + v.toString()));
                System.out.println("Printing all users:");
                userRepository.findAll().forEach(v ->  System.out.println(" - User :" + v.toString()));
                System.out.println("Printing all teams:");
                teamRepository.findAll().forEach(v ->  System.out.println(" - Team :" + v.toString()));

            }
        };
    }

    /* generates mock data (3 courses, admin, 2 students, team, teacher) */
    public void generateMockData(CourseRepository cr, VmModelRepository vmr, TeamRepository tr, UserRepository ur, PasswordEncoder passwordEncoder, StudentRepository sr, TeamService teamService, NotificationService notificationService, TokenTeamRepository ttr, TeacherRepository tcr) {
        if(!ur.findAll().stream().anyMatch(u -> u.getUsername().equals("admin@polito.it"))) {
            try {
                //Course: PDS
                Course newCourse = Course.builder()
                        .name("Programmazione di Sistema")
                        .acronym("PDS")
                        .enabled(true)
                        .min(2)
                        .max(4)
                        .build();
                cr.save(newCourse);
                VmModel newVmModel = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(newCourse)
                        .maxVms(6)
                        .maxRunningVms(3)
                        .maxVcpus(6*5)
                        .maxRam(6*8)
                        .maxDisk(6*500)
                        .build();
                vmr.save(newVmModel);
                //Course: ML
                newCourse = Course.builder()
                        .name("Machine Learning")
                        .acronym("ML")
                        .enabled(true)
                        .min(3)
                        .max(6)
                        .build();
                cr.save(newCourse);
                newVmModel = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(newCourse)
                        .maxVms(6)
                        .maxRunningVms(3)
                        .maxVcpus(6*5)
                        .maxRam(6*8)
                        .maxDisk(6*500)
                        .build();
                vmr.save(newVmModel);
                //Course: AI
                newCourse = Course.builder()
                        .name("Applicazioni Internet")
                        .acronym("AI")
                        .enabled(false)
                        .min(5)
                        .max(10)
                        .build();
                cr.save(newCourse);
                newVmModel = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(newCourse)
                        .maxVms(6)
                        .maxRunningVms(3)
                        .maxVcpus(6*5)
                        .maxRam(6*8)
                        .maxDisk(6*500)
                        .build();
                vmr.save(newVmModel);

                //User-Admin
                ur.save(User.builder()
                        .id("s000000")
                        .username("admin@polito.it")
                        .enabled(true)
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT", "ROLE_PROF", "ROLE_ADMIN"))
                        .build()
                );
                //User-teacher
                ur.save(User.builder()
                        .id("d654321")
                        .username("teacher@polito.it")
                        .enabled(true)
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_PROF"))
                        .build()
                );
                Teacher newTeacher  = Teacher.builder()
                        .id("d654321")
                        .email("teacher@polito.it")
                        .lastName("Paperoni")
                        .name("Paperon")
                        .build();
                tcr.save(newTeacher);
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
                Student newStudent = Student.builder()
                        .id("s123456")
                        .email("s123456@studenti.polito.it")
                        .lastName("Rossi")
                        .name("Mario")
                        .build();
                sr.save(newStudent);
                //User-Student: s234567 (Giacomo Bianchi)
                ur.save(User.builder()
                        .id("s234567")
                        .username("s234567@studenti.polito.it")
                        .password(passwordEncoder.encode("pwd"))
                        .roles(Arrays.asList("ROLE_STUDENT"))
                        .enabled(true)
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
                        .enabled(true)
                        .build()
                );
                newStudent = Student.builder()
                        .id("s345678")
                        .email("s345678@studenti.polito.it")
                        .lastName("Dario")
                        .name("Verdi")
                        .build();
                sr.save(newStudent);
            } catch (Exception e) {
                System.out.println("Exception insert mock data: " + e.getMessage());
            } finally {
                SecurityContextHolder.clearContext();
            }
        }
    }

}
