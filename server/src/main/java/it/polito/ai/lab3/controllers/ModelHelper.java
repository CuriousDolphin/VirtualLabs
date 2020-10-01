package it.polito.ai.lab3.controllers;

import it.polito.ai.lab3.dtos.CourseDTO;
import it.polito.ai.lab3.dtos.StudentDTO;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ModelHelper {
    public static CourseDTO enrich(CourseDTO courseDTO) {
        Link selfLink =
                linkTo(CourseController.class)
                        .slash(courseDTO.getName())
                        .withSelfRel();

        Link enrolledLink =
                linkTo(methodOn(CourseController.class)
                .enrolledStudents(courseDTO.getName()))
                .withRel("enrolled");

        courseDTO.add(selfLink);
        courseDTO.add(enrolledLink);
        return courseDTO;
    }

    public static StudentDTO enrich(StudentDTO studentDTO) {
        Link link = linkTo(StudentController.class).slash(studentDTO.getId()).withSelfRel();
        studentDTO.add(link);
        return studentDTO;
    }
}
