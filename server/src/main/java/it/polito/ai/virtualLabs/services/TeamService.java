package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.CourseDTO;
import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    boolean addCourse(CourseDTO course);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    CourseDTO updateCourse(CourseDTO course,String courseName);

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

    // multiple
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<Boolean> removeStudentsFromCourse(List<String> studentIds, String courseName);

    // single
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    boolean removeStudentFromCourse(String studentId, String courseName);

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

    // assumo che l'username del principal sia lo stesso dell'id dello studente
    @PreAuthorize("(#studentId == authentication.principal.id and hasRole('ROLE_STUDENT')) or hasRole('ROLE_ADMIN')")
    List<CourseDTO> getCourses(String studentId);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    List<TeamDTO> getTeamsForStudent(String studentId);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    List<TeamDTO> getTeamsForStudentCourse(String studentId,String courseName);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<StudentDTO> getMembers(Long teamId);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    TeamDTO proposeTeam(String courseName, String name, List<String> memberIds ,String ownerId, Integer timeoutDays);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') ")
    List<TeamDTO> getTeamsForCourse(String courseName);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or hasRole('ROLE_STUDENT')")
    List<StudentDTO> getStudentsInTeams(String courseName);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or hasRole('ROLE_STUDENT')")
    List<StudentDTO> getAvailableStudents(String courseName);


    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') ")
    void activateTeam(Long teamId);
    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') ")
    void evictTeam(Long teamId);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') ")
    List<TeamDTO> getPendingTeamsForStudent(String studentId);
}
