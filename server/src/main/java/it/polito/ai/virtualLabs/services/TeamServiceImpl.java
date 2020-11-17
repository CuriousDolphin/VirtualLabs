package it.polito.ai.virtualLabs.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtualLabs.dtos.CourseDTO;
import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.entities.Course;
import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.entities.Team;
import it.polito.ai.virtualLabs.entities.TeamToken;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.repositories.CourseRepository;
import it.polito.ai.virtualLabs.repositories.StudentRepository;
import it.polito.ai.virtualLabs.repositories.TeamRepository;
import it.polito.ai.virtualLabs.repositories.TeamTokenRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamTokenRepository tokenRepository;


    @Autowired
    ModelMapper modelMapper;


    @Override
    public boolean addCourse(CourseDTO course) {
        if (courseRepository.findByNameIgnoreCase(course.getName()).isPresent()) {
            return false;
        } else {
            if (course.getName() != null && !course.getName().equals("")) {
                courseRepository.save(modelMapper.map(course, Course.class));
                return true;
            }
            return false;
        }
    }

    @Override
    public CourseDTO updateCourse(CourseDTO course, String courseName){
        //if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException();
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();

        Course c = courseRepository.findByNameIgnoreCase(courseName).get();
        c.setAcronym(course.getAcronym());
        c.setEnabled(course.isEnabled());
        c.setMax(course.getMax());
        c.setMin(course.getMin());
        c.setName(course.getName());

        this.courseRepository.save(c);

        return modelMapper.map(c,CourseDTO.class);

    }

    @Override
    public Optional<CourseDTO> getCourse(String name) {

        if (!courseRepository.findByNameIgnoreCase(name).isPresent()) throw new CourseNotFoundException();

        //if (!courseRepository.existsById(name)) throw new CourseNotFoundException();

        Course course = courseRepository.findByNameIgnoreCase(name).get();
        System.out.println("GET COURSE " + course.getName());
        return courseRepository
                .findByNameIgnoreCase(name)
                .map(course1 -> modelMapper.map(course1, CourseDTO.class));


    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override

    public boolean addStudent(StudentDTO student) {

        if (studentRepository.existsById(student.getId())) {
            return false;
        } else {
            studentRepository.save(modelMapper.map(student, Student.class));
            return true;
        }
    }

    @Override
    public Optional<StudentDTO> getStudent(String studentId) {
        return studentRepository.findByIdIgnoreCase(studentId)
                .map(student -> modelMapper.map(student, StudentDTO.class));
    }


    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();
        return courseRepository
                //.findById(courseName)
                .findByNameIgnoreCase(courseName).get()
                .getStudents()
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addStudentToCourse(String studentId, String courseName) {

        if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException();
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();
        if (courseRepository.findByNameIgnoreCase(courseName).get().getStudents().contains(studentRepository.findByIdIgnoreCase(studentId).get()))
            return false;

        Course c = courseRepository.findByNameIgnoreCase(courseName).get();
        System.out.println("ADD STUDENT TO COURSE " + c.getName());
        Student s = studentRepository.findByIdIgnoreCase(studentId).get();
        System.out.println("ADD STUDENT TO COURSE STUDENT" + s.getId());
        c.addStudent(s);

        return true;
    }
    // unenroll multiple students
    @Override
    public List<Boolean> removeStudentsFromCourse(List<String> studentIds, String courseName){
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();


        Course c = courseRepository.findByNameIgnoreCase(courseName).get();
        System.out.println("REMOVE STUDENTS TO COURSE " + c.getName());

        List<Boolean> ris = new ArrayList<>();
        studentIds.forEach(s -> {
            try {
                ris.add(this.removeStudentFromCourse(s, courseName));
            } catch (Exception e) {
                System.out.println("catched exception " + e.toString());
                ris.add(false);
            }
        });
        return ris;


    }
    // enroll single student
    @Override
    public boolean removeStudentFromCourse(String studentId, String courseName){
        if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException();

        Course c = courseRepository.findByNameIgnoreCase(courseName).get();
        System.out.println("REMOVE STUDENT TO COURSE " + c.getName());
        Student s = studentRepository.findByIdIgnoreCase(studentId).get();
        System.out.println(" STUDENT" + s.getId());

        c.removeStudent(s);
        return true;


    }

    @Override
    public void enableCourse(String courseName) {

        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();
        courseRepository.findByNameIgnoreCase(courseName).get().setEnabled(true);
    }

    @Override
    public void disableCourse(String courseName) {

        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();
        courseRepository.findByNameIgnoreCase(courseName).get().setEnabled(false);

    }

    @Override
    public List<Boolean> addAll(List<StudentDTO> students) {
        List<Boolean> ris = new ArrayList<>();
        students.forEach(studentDTO -> {
            System.out.println("st " + studentDTO.toString());
            ris.add(this.addStudent(studentDTO));
        });
        return ris;
    }

    @Override

    public List<Boolean> enrollAll(List<String> studentIds, String courseName) {

        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();
        List<Boolean> ris = new ArrayList<>();
        studentIds.forEach(s -> {
            try {
                ris.add(this.addStudentToCourse(s, courseName));
            } catch (Exception e) {
                System.out.println("catched exception " + e.toString());
                ris.add(false);
            }
        });
        return ris;
    }

    @Override

    public List<Boolean> addAndEnroll(Reader r, String courseName) {
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();
        /* create csv bean reader */
        CsvToBean csvToBean = new CsvToBeanBuilder(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        // convert `CsvToBean` object to list of users
        List<StudentDTO> students = csvToBean.parse();
        List<String> studentIds = new ArrayList<>();
        students.forEach(student -> {
            studentIds.add(student.getId());
            System.out.println(student.toString());
        });

        List<Boolean> l1 = this.addAll(students);
        List<Boolean> l2 = this.enrollAll(studentIds, courseName);
        /*List<Boolean> ris = new ArrayList<>();

        for(int i = 0; i< l2.size(); i++){
            ris.add( l1.get(i)&&l2.get(i));
        }*/

        return l2;
    }

    @Override

    public List<CourseDTO> getCourses(String studentId) {
        if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException();
        return this.studentRepository.findByIdIgnoreCase(studentId)
                .get()
                .getCourses()
                .stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeamsForStudent(String studentId) {
        if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException();
        return this.studentRepository.findByIdIgnoreCase(studentId)
                .get()
                .getTeams()
                .stream()
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<TeamDTO> getTeamsForStudentCourse(String studentId,String courseName) {
        if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException();
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();

        return this.studentRepository.findByIdIgnoreCase(studentId)
                .get()
                .getTeams()
                .stream()
                .filter(team ->
                     team.getCourse().getName().toLowerCase().equals(courseName.toLowerCase())
                )
                .map(team ->
                    modelMapper.map(team, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getMembers(Long teamId) {
        if (!teamRepository.existsById(teamId)) throw new TeamNotFoundException();
        return this.teamRepository.getOne(teamId)
                .getMembers()
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override

    public TeamDTO proposeTeam(String courseId, String name, List<String> memberIds) {
        // check esistenza corso
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException();

        // course enabled
        Course course = courseRepository.findByNameIgnoreCase(courseId).get();
        if (!course.isEnabled())
            throw new CourseNotEnabled();

        // course limit min and max
        if (memberIds.size() > course.getMax() || memberIds.size() < course.getMin())
            throw new CourseMinMax();

        List<String> tmp = new ArrayList<>();
        memberIds.forEach(
                studentId -> {
                    // check esistenza studente
                    if (!studentRepository.existsById(studentId))
                        throw new StudentNotFoundException();

                    Student s = studentRepository.findByIdIgnoreCase(studentId).get();

                    // check studente iscritto al corso
                    if (!s.getCourses().contains(course))
                        throw new StudentNotEnrolled();

                    // check studente iscritto ad altri team nello stesso corso
                    s.getTeams().forEach(team -> {
                        if (team.getCourse().equals(course))
                            throw new StudentAlreadyHaveTeam();
                    });

                    // check studente duplicato
                    if (tmp.contains(studentId))
                        throw new StudentDuplicate();

                    tmp.add(studentId);
                }
        );

        // inserimento team
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName(name);
        Team newTeam = teamRepository.save(modelMapper.map(teamDTO, Team.class));
        System.out.println("new team saved " + newTeam.toString());

        newTeam.setCourse(course);

        memberIds.forEach(
                s -> {
                    newTeam.addMembers(studentRepository.findByIdIgnoreCase(s).get());
                }
        );

        course.addTeam(newTeam);

        return modelMapper.map(newTeam, TeamDTO.class);
    }


    @Override
    public List<TeamDTO> getTeamsForCourse(String courseName) {
        // check esistenza corso
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();


        return courseRepository.findByNameIgnoreCase(courseName)
                .get()
                .getTeams()
                .stream()
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .collect(Collectors.toList());

    }

    @Override

    public List<StudentDTO> getStudentsInTeams(String courseName) {
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();


        return courseRepository.getStudentsInTeams(courseName)
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getAvailableStudents(String courseName) {
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();


        return courseRepository.getStudentsNotInTeams(courseName)
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public List<TeamDTO> getPendingTeamsForStudent(String studentId){
        if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException();

        List<TeamToken> tokens = tokenRepository.findAllByStudentId(studentId);

        List<TeamDTO> tmp = null;

        tokens.forEach(teamToken ->
                    tmp.add(modelMapper.map(teamToken.getTeam(),TeamDTO.class))
                );
        return  tmp;


    }


    @Override
    public void activateTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) throw new TeamNotFoundException();
        teamRepository.getOne(teamId).setStatus(1);
    }

    @Override
    public void evictTeam(Long teamId) {
        System.out.println("GOING TO EVICT " + teamId.toString());
        if (!teamRepository.existsById(teamId)) throw new TeamNotFoundException();
        Team t = teamRepository.getOne(teamId);
        teamRepository.delete(t);
        ///teamRepository.deleteById(teamId);
    }
}
