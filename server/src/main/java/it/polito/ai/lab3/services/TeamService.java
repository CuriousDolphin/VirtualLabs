package it.polito.ai.lab3.services;

import it.polito.ai.lab3.dtos.CourseDTO;
import it.polito.ai.lab3.dtos.StudentDTO;
import it.polito.ai.lab3.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    boolean addCourse(CourseDTO course);

    Optional<CourseDTO> getCourse(String name);

    List<CourseDTO> getAllCourses();

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    boolean addStudent(StudentDTO student);

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<StudentDTO> getEnrolledStudents(String courseName);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    boolean addStudentToCourse(String studentId, String courseName);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    void enableCourse(String courseName);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    void disableCourse(String courseName);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<Boolean> addAll(List<StudentDTO> students);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<Boolean> enrollAll(List<String> studentIds, String courseName);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<Boolean> addAndEnroll(Reader r, String courseName);

    // assumo che l'username del principal sia lo steddo dell'id dello studente
    @PreAuthorize("(#studentId == authentication.principal.username and hasRole('ROLE_STUDENT')) or hasRole('ROLE_ADMIN')")
    List<CourseDTO> getCourses(String studentId);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<TeamDTO> getTeamsForStudent(String studentId);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<StudentDTO> getMembers(Long teamId);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    TeamDTO proposeTeam(String courseId,String name,List<String> memberIds);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or hasRole('ROLE_STUDENT')")
    List<TeamDTO> getTeamForCourse(String courseName);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or hasRole('ROLE_STUDENT')")
    List<StudentDTO> getStudentsInTeams(String courseName);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or hasRole('ROLE_STUDENT')")
    List<StudentDTO> getAvailableStudents(String courseName);

    void activateTeam(Long teamId);
    void evictTeam(Long teamId);
}
