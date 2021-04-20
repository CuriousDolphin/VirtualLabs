package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.CourseProposal;
import it.polito.ai.virtualLabs.TeamProposal;
import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.repositories.VmModelRepository;
import it.polito.ai.virtualLabs.services.NotificationService;
import it.polito.ai.virtualLabs.services.TeamService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    TeamService teamService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    NotificationService notificationService;

    @Autowired
    VmModelRepository vmr;

    @GetMapping({"", "/"})
    List<CourseDTO> all() {

        return teamService
                .getAllCourses()
                .stream()
                .map(courseDTO -> ModelHelper.enrich(courseDTO))
                .collect(Collectors.toList());

    }

    @GetMapping({"/teacher/{userId}"})
    List<CourseDTO> getAllByTeacher(@PathVariable("userId") String userId) {

        return teamService
                .getAllTeacherCourses(userId)
                .stream()
                .map(courseDTO -> ModelHelper.enrich(courseDTO))
                .collect(Collectors.toList());

    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    CourseDTO addCourse(@Valid @RequestBody(required = true) CourseProposal body, BindingResult result) {
        CourseDTO courseDTO = body.getCourse();
        String userId = body.getUserId();

        if (result.hasErrors()) throw new ResponseStatusException(HttpStatus.CONFLICT);
        if (!teamService.addCourse(courseDTO,userId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseDTO.getName());
        // notificationService.sendMessage("isnob46@gmail.com","nuova materia inserita",dto.getName().toString());
        return ModelHelper.enrich(courseDTO);

    }

    @PostMapping("/{name}/enrollOne")
    @ResponseStatus(HttpStatus.CREATED)
    void enrollOne(@RequestBody Map<String, String> input, @PathVariable("name") String courseName) {
        if (!input.containsKey("id")) throw new ResponseStatusException(HttpStatus.CONFLICT);
        try {
            if (!teamService.addStudentToCourse(input.get("id"), courseName))
                throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (CourseNotFoundException ce) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        } catch (StudentNotFoundException se) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
    }


    @PatchMapping("/{name}/unEnrollMany")
    List<Boolean> unenrollMany(@RequestBody List<String> studentIds,@PathVariable("name") String courseName){
        try {
            List<Boolean> ris = teamService.removeStudentsFromCourse(studentIds, courseName);
            return ris;
        } catch (CourseNotFoundException ce) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }
    // csv
    @PostMapping("/{name}/addAndEnroll")
    List<Boolean> enrollMany(@RequestParam("file") MultipartFile file, @PathVariable("name") String courseName) {

        if (!file.getContentType().equals("text/csv") && !file.getContentType().equals("application/vnd.ms-excel"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, file.getContentType());
        if (file.isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT);
        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            List<Boolean> ris = teamService.addAndEnroll(reader, courseName);
            return ris;
        } catch (CourseNotFoundException ce) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/{name}/proposeTeam")
    @ResponseStatus(HttpStatus.CREATED)
    TeamDTO proposeTeam(@Valid @RequestBody(required = true) TeamProposal proposal, @PathVariable("name") String courseName, BindingResult result) {

        try {
            System.out.println("_______________________"+proposal.toString());

            TeamDTO team = teamService.proposeTeam(courseName, proposal.getName(), proposal.getMembers(),proposal.getOwner(),proposal.getDaysTimeout());
            notificationService.notifyTeam(team,proposal.getMembers(),proposal.getDaysTimeout());

            return team;



        } catch (CourseNotFoundException ce) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        } catch (StudentNotFoundException se) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        } catch (StudentNotEnrolled se) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student not enrolled");
        } catch (StudentDuplicate se) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student  DUPLICATE");
        } catch (StudentAlreadyHaveTeam se) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student  already have a enabled team");
        } catch (Exception e) {
            System.out.println("___________________________________________________"+e.toString());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }


    @GetMapping("/{name}")
    CourseDTO getOne(@PathVariable("name") String name) {
        try{
            Optional<CourseDTO> course = teamService.getCourse(name);
            if (course.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, name);
            return ModelHelper.enrich(course.get());


        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @GetMapping("/{name}/teams")
    List<TeamDTO> getTeams(@PathVariable("name") String name) {
        try{
            return teamService.getTeamsForCourse(name);


        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @GetMapping("/{name}/teams/{idStudent}")
    List<TeamDTO> getStudentCourseTeam(@PathVariable("name") String name,@PathVariable("idStudent") String idStudent) {
        try{
            return teamService.getTeamsForStudentCourse(idStudent,name);
        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @GetMapping("/{name}/pendingTeams/{idStudent}")
    List<TeamDTO> getStudentPendingTeams(@PathVariable("name") String name,@PathVariable("idStudent") String idStudent) {
        try{
            return teamService.getPendingTeamsForStudent(idStudent);

        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @GetMapping("/{name}/studentsInTeam")
    List<StudentDTO> getStudentsInTeam(@PathVariable("name") String name) {
        try{
            return teamService.getStudentsInTeams(name);
        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }
    @GetMapping("/{name}/studentsNotInTeam")
    List<StudentDTO> getStudentsNotInTeam(@PathVariable("name") String name) {
        try{
            return teamService.getAvailableStudents(name);
        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }


    @GetMapping("/{name}/enrolled")
    List<EnrolledStudentDTO> enrolledStudents(@PathVariable("name") String name) {
        try{
            return teamService.getEnrolledStudents(name);
        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @GetMapping("/assignments/{id}")
    AssignmentDTO getAssignment(@PathVariable("id") Long assignmentId) {
        try {
            return teamService.getAssignment(assignmentId);
        } catch (AssignmentNotFoundException assignmentNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }
    }

    @GetMapping("/papers/{id}")
    PaperDTO getPaper(@PathVariable("id") Long paperId) {
        try {
            return teamService.getPaper(paperId);
        } catch (PaperNotFoundException paperNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper nof found");
        }
    }

    @GetMapping("/{name}/assignments")
    List<AssignmentDTO> getAllAssignmentsForCourse(@PathVariable("name") String courseName) {
        try {
            return teamService.getAllAssignmentsForCourse(courseName);
        } catch (CourseNotFoundException courseNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @PatchMapping("/{name}")
    CourseDTO updateCourse(@RequestBody @Valid CourseProposal body, @PathVariable("name") String courseName) {
        try {
            CourseDTO c=teamService.updateCourse(body.getCourse(),courseName,body.getUserId());
            return c;
        } catch (CourseNotFoundException ce) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @GetMapping("/assignments/{id}/papers")
    List<PaperDTO> getAllPapersForAssignment(@PathVariable("id") Long assignmentId) {
        try {
            return teamService.getAllPapersForAssignment(assignmentId);
        } catch (AssignmentNotFoundException assignmentNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }
    }

    @GetMapping("/assignments/papers/{id}/papersnapshots")
    List<PaperSnapshotDTO> getAllPaperSnapshotForPaper(@PathVariable("id") Long paperId) {
        try {
            return teamService.getAllPaperSnapshotsForPaper(paperId);
        }   catch (PaperNotFoundException paperNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found");
        }
    }

    @PostMapping("{courseName}/assignments/addAssignment")
    @ResponseStatus(HttpStatus.CREATED)
    AssignmentDTO addAssignmentToCourse(@PathVariable("courseName") String courseName, @Valid @RequestBody AssignmentDTO assignmentDTO) {
        try {
            return teamService.addAssignmentToCourse(
                    courseName,
                    assignmentDTO);
        } catch (CourseNotFoundException courseNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @PostMapping("/assignments/papers/{paperId}/papersnapshots/addPapersnapshot")
    @ResponseStatus(HttpStatus.CREATED)
    PaperSnapshotDTO addPaperSnapshot(@PathVariable("paperId") Long paperId, @Valid @RequestBody FormPaperSnapshotDataDTO formPaperSnapshotDataDTO) {
        try {
            return teamService.addPaperSnapshotToPaper(
                    paperId,
                    formPaperSnapshotDataDTO.getPapersnapshot(),
                    formPaperSnapshotDataDTO.getToReview(),
                    formPaperSnapshotDataDTO.getVote());
        }
        catch (PaperNotFoundException paperNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found");
        } catch (AssignmentExpiredException assignmentExpiredException) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed");
        }
    }
    @GetMapping("/{name}/vmInstances")
    List<VmInstanceDTO> vmInstances(@PathVariable("name") String name) {
        return teamService.getVmInstancesPerCourse(name);
    }

    @GetMapping("/{name}/vmmodel")
    VmModelDTO vmModels(@PathVariable("name") String name) {
        return teamService.getVmModel(name);
    }

    @PostMapping({"/{name}/editvmmodel"})
    VmModelDTO editVmModel(@PathVariable("name") String name, @Valid @RequestBody(required = true) VmModelDTO vmModel) {
        return teamService.editVmModel(name, vmModel);
    }

}
