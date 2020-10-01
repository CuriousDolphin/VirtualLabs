package it.polito.ai.lab3.controllers;

import it.polito.ai.lab3.dtos.CourseDTO;
import it.polito.ai.lab3.dtos.StudentDTO;
import it.polito.ai.lab3.exceptions.StudentNotFoundException;
import it.polito.ai.lab3.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/students")
public class StudentController {
    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    List<StudentDTO> all() {
        return teamService.getAllStudents()
                .stream()
                .map(studentDTO -> ModelHelper.enrich(studentDTO))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    StudentDTO getOne(@PathVariable("id") String id) {
        Optional<StudentDTO> student = teamService.getStudent(id);
        if (student.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, id);
        return ModelHelper.enrich(student.get());
    }


    // solo gli admin o gli user con username=id_studente possono accederci
    @GetMapping("/{id}/courses")
    List<CourseDTO> getCourses(@PathVariable("id") String id) {
        try{
            return teamService.getCourses(id)
                    .stream()
                    .map(courseDTO -> ModelHelper.enrich(courseDTO))
                    .collect(Collectors.toList());

        }catch(StudentNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, id);
        }

    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    StudentDTO addStudent(@Valid @RequestBody StudentDTO studentDTO, BindingResult result){
        if(result.hasErrors())  throw new ResponseStatusException(HttpStatus.CONFLICT);
        if(!teamService.addStudent(studentDTO))
            throw new ResponseStatusException(HttpStatus.CONFLICT, studentDTO.getName());
        return ModelHelper.enrich(studentDTO);

    }



}
