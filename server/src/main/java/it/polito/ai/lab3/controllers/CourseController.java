package it.polito.ai.lab3.controllers;

import it.polito.ai.lab3.TeamProposal;
import it.polito.ai.lab3.dtos.CourseDTO;
import it.polito.ai.lab3.dtos.StudentDTO;
import it.polito.ai.lab3.dtos.TeamDTO;
import it.polito.ai.lab3.exceptions.*;
import it.polito.ai.lab3.services.NotificationService;
import it.polito.ai.lab3.services.TeamService;
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
@RequestMapping("/API/courses")
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

    @PostMapping("/{name}/enrollMany")
    List<Boolean> enrollMany(@RequestParam("file") MultipartFile file, @PathVariable("name") String courseName) {
        if (!file.getContentType().equals("text/csv"))
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

            TeamDTO team = teamService.proposeTeam(courseName, proposal.getName(), proposal.getMembers());
            notificationService.notifyTeam(team,proposal.getMembers());

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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student  already have a team");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.toString());
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
            return teamService.getTeamForCourse(name);


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
    void updateCourse(@RequestBody @Valid Map<String, Boolean> input, @PathVariable("name") String courseName) {
        if (!input.containsKey("enabled")) throw new ResponseStatusException(HttpStatus.CONFLICT);
        try {
            if(input.get("enabled")==true){
                teamService.enableCourse(courseName);
            }else{
                teamService.disableCourse(courseName);
            }
        } catch (CourseNotFoundException ce) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }


}
