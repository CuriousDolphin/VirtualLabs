package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.TeamProposal;
import it.polito.ai.virtualLabs.dtos.CourseDTO;
import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.services.NotificationService;
import it.polito.ai.virtualLabs.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
    NotificationService notificationService;

    @GetMapping({"", "/"})
    List<CourseDTO> all() {

        return teamService
                .getAllCourses()
                .stream()
                .map(courseDTO -> ModelHelper.enrich(courseDTO))
                .collect(Collectors.toList());

    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    CourseDTO addCourse(@Valid @RequestBody(required = true) CourseDTO dto, BindingResult result) {
        if (result.hasErrors()) throw new ResponseStatusException(HttpStatus.CONFLICT);
        if (!teamService.addCourse(dto))
            throw new ResponseStatusException(HttpStatus.CONFLICT, dto.getName());
        // notificationService.sendMessage("isnob46@gmail.com","nuova materia inserita",dto.getName().toString());
        return ModelHelper.enrich(dto);

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
    List<StudentDTO> enrolledStudents(@PathVariable("name") String name) {
        try{
            return teamService.getEnrolledStudents(name);
        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @PatchMapping("/{name}")
    CourseDTO updateCourse(@RequestBody @Valid CourseDTO course, @PathVariable("name") String courseName) {

        try {
            CourseDTO c=teamService.updateCourse(course,courseName);
            return c;
        } catch (CourseNotFoundException ce) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }


}
