package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TeamService {
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    boolean addCourse(CourseDTO course, String userId);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    CourseDTO updateCourse(CourseDTO course,String courseName, String userId);

    Optional<CourseDTO> getCourse(String name);

    List<CourseDTO> getAllCourses();

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    boolean addStudent(StudentDTO student);

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<CourseDTO> getAllTeacherCourses(String userId);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<EnrolledStudentDTO> getEnrolledStudents(String courseName);

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

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    AssignmentDTO getAssignment(Long assignmentId);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<AssignmentDTO> getAllAssignmentsForCourse(String courseName);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<PaperDTO> getAllPapersForAssignment(Long assignmentId);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    List<PaperSnapshotDTO> getAllPaperSnapshotsForPaper(Long paperId);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    PaperSnapshotDTO addPaperSnapshotToPaper(Long paperId, PaperSnapshotDTO paperSnapshotDTO, boolean toReview, Integer vote);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') ")
    void activateTeam(Long teamId);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') ")
    void evictTeam(Long teamId);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') ")
    List<TeamDTO> getPendingTeamsForStudent(String studentId);

    /* VMs */

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or (#id == authentication.principal.id and hasRole('ROLE_STUDENT'))")
    List<VmInstanceDTO> getVmInstances(@Param("id") String studentId, String team);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or (#id == authentication.principal.id and hasRole('ROLE_STUDENT'))")
    VmInstanceDTO createVmInstance(@Param("id") String studentId, String team, VmInstanceDTO vmInstance);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or (#id == authentication.principal.id and hasRole('ROLE_STUDENT'))")
    VmInstanceDTO startVmInstance(@Param("id") String studentId, String team, Long idVmInstance);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or (#id == authentication.principal.id and hasRole('ROLE_STUDENT'))")
    VmInstanceDTO stopVmInstance(@Param("id") String studentId, String team, Long idVmInstance);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or (#id == authentication.principal.id and hasRole('ROLE_STUDENT'))")
    List<VmInstanceDTO> deleteVmInstance(@Param("id") String studentId, String team, Long idVmInstance);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or (#id == authentication.principal.id and hasRole('ROLE_STUDENT'))")
    VmInstanceDTO editVmInstance(@Param("id") String studentId, String team, Long idVmInstance, VmInstanceDTO vmInstance);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    VmModelDTO getVmModel(String course);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') or (#id == authentication.principal.id and hasRole('ROLE_STUDENT'))")
    VmModelDTO getVmModel(String id, String team);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF') ")
    List<VmInstanceDTO> getVmInstancesPerCourse(@Param("courseName") String course);

    @PreAuthorize(" hasRole('ROLE_ADMIN') or hasRole('ROLE_PROF')")
    VmModelDTO editVmModel(String course, VmModelDTO vmModel);




}
