package it.polito.ai.virtualLabs.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.repositories.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.*;
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
    TokenTeamRepository tokenRepository;

    @Autowired
    PaperRepository paperRepository;

    @Autowired
    PaperSnapshotRepository paperSnapshotRepository;

    @Autowired
    AssignmentRepository assignmentRepository;
    /*
    @Autowired
    VmConfigurationRepository vmConfigurationRepository;
    */
    @Autowired
    VmModelRepository vmModelRepository;


    @Autowired
    ModelMapper modelMapper;

    @Autowired
    VmInstanceRepository vmInstanceRepository;

    @Autowired
    TeacherRepository teacherRepository;


    @Override
    public boolean addCourse(CourseDTO course, String userId) {
        if (courseRepository.findByNameIgnoreCase(course.getName()).isPresent()) {
            return false;
        } else {
            if (course.getName() != null && !course.getName().equals("")) {
                Course newCourse = modelMapper.map(course, Course.class);
                Optional<Teacher> optTeacher = teacherRepository.findById(userId);
                if (optTeacher.isPresent())
                    newCourse.addTeacher(optTeacher.get());
                else
                    return false;
                if (!optTeacher.get().getId().equals("admin"))
                    newCourse.addTeacher(teacherRepository.getOne("admin"));
                VmModel newVmModel = VmModel.builder()
                        .image("defaultVmImage.png")
                        .course(newCourse)
                        .maxVms(6)
                        .maxRunningVms(3)
                        .maxVcpus(6 * 5)
                        .maxRam(6 * 8)
                        .maxDisk(6 * 500)
                        .build();
                vmModelRepository.save(newVmModel);
                courseRepository.save(newCourse);
                return true;
            }
            return false;
        }
    }

    @Override
    public CourseDTO updateCourse(CourseDTO course, String courseName, String userId) {
        //if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException();
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();

        Course c = courseRepository.findByNameIgnoreCase(courseName).get();
        Optional<Teacher> optTeacher = teacherRepository.findById(userId);
        if (!optTeacher.isPresent())
            throw new CourseNotFoundException();
        if (!c.getTeachers().contains(optTeacher.get()))
            throw new CourseNotFoundException();
        c.setAcronym(course.getAcronym());
        c.setEnabled(course.isEnabled());
        c.setMax(course.getMax());
        c.setMin(course.getMin());
        c.setName(course.getName());

        this.courseRepository.save(c);

        return modelMapper.map(c, CourseDTO.class);

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
    public List<EnrolledStudentDTO> getEnrolledStudents(String courseName) {
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();
        List<Student> students = courseRepository
                //.findById(courseName)
                .findByNameIgnoreCase(courseName).get()
                .getStudents();

        List<EnrolledStudentDTO> res = new ArrayList<>();


        // add preferred tea for course
        students.forEach(student -> {
            EnrolledStudentDTO std = modelMapper.map(student, EnrolledStudentDTO.class);
            System.out.println(std);
            student.getTeams().forEach(team -> {
                System.out.println(team);

                if (team.getCourse().getName().toLowerCase().equals(courseName.toLowerCase()) && team.getStatus() == 1) {
                    std.setTeamName(team.getName());
                }
            });
            res.add(std);

        });

        return res;

        /*return courseRepository
                //.findById(courseName)
                .findByNameIgnoreCase(courseName).get()
                .getStudents()
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());*/
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
    public List<Boolean> removeStudentsFromCourse(List<String> studentIds, String courseName) {
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
    public boolean removeStudentFromCourse(String studentId, String courseName) {
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
    public List<TeamDTO> getTeamsForStudentCourse(String studentId, String courseName) {
        if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException();
        if (!courseRepository.findByNameIgnoreCase(courseName).isPresent()) throw new CourseNotFoundException();

        List<TeamDTO> teams = this.studentRepository.findByIdIgnoreCase(studentId)
                .get()
                .getTeams()
                .stream()
                .filter(team ->
                        team.getCourse().getName().toLowerCase().equals(courseName.toLowerCase())
                )
                .map(team ->
                        modelMapper.map(team, TeamDTO.class))
                .collect(Collectors.toList());

        // check status for each
        System.out.println(teams.toString());

        teams.forEach(
                teamDTO -> {


                    Map<String, String> studentsStatus = new java.util.HashMap<>(Map.of());
                    teamDTO.getMembers().forEach(
                            studentDTO -> {
                                System.out.println("=======" + studentDTO.getId());

                                if (!tokenRepository.existsByTeamAndStudentId(modelMapper.map(teamDTO, Team.class), studentDTO.getId())) {
                                    studentsStatus.put(studentDTO.getId(), "Confirmed");
                                } else {
                                    studentsStatus.put(studentDTO.getId(), "Pending");

                                    // se e' un team pendente aggiungo ulteriori informazioni
                                    if (studentDTO.getId().equals(studentId)) {
                                        TokenTeam token = tokenRepository.getByTeamAndStudentId(modelMapper.map(teamDTO, Team.class), studentId);

                                        teamDTO.setConfirmation_token(token.getId());
                                        teamDTO.setExpiry_date(token.getExpiryDate());
                                    }
                                }
                            }
                    );

                    teamDTO.setMembers_status(studentsStatus);

                });


        return teams;
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

    public TeamDTO proposeTeam(String courseName, String name, List<String> memberIds, String ownerId, Integer timeoutDays) {
        Student owner = studentRepository.findByIdIgnoreCase(ownerId).get();
        // check esistenza corso
        if (!courseRepository.existsCourseByName(courseName))
            throw new CourseNotFoundException();
        // course enabled
        Course course = courseRepository.findByNameIgnoreCase(courseName).get();
        if (!course.isEnabled())
            throw new CourseNotEnabled();

        // course limit min and max
        if ((memberIds.size() + 1) > course.getMax() || (memberIds.size() + 1) < course.getMin())
            throw new CourseMinMax();
        List<Student> members = new ArrayList<>();
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
                        if (team.getCourse().equals(course) && team.getStatus() == 1)
                            throw new StudentAlreadyHaveTeam();
                    });

                    // check studente duplicato
                    if (members.contains(s))
                        throw new StudentDuplicate();

                    members.add(s);
                }
        );

        members.add(owner);

        // inserimento team
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName(name);
        Team newTeam = modelMapper.map(teamDTO, Team.class);
        newTeam.setOwner(owner);
        newTeam.setCourse(course);
        newTeam.setMembers(members);
        teamRepository.save(newTeam);
        System.out.println("new team saved " + newTeam.toString());

        course.addTeam(newTeam);
        System.out.println("=======================8");


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
        if (courseRepository.findByNameIgnoreCase(courseName).isEmpty()) throw new CourseNotFoundException();


        return courseRepository.getStudentsNotInTeams(courseName)
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public AssignmentDTO getAssignment(Long assignmentId) {
        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
        if (!assignment.isPresent()) throw new AssignmentNotFoundException();

        AssignmentDTO assignmentDTO = modelMapper.map(assignment.get(), AssignmentDTO.class);
        String base64 = "data:image/png;base64," + Base64.getMimeEncoder().encodeToString(assignment.get().getContent());
        assignmentDTO.setContent(base64);

        return assignmentDTO;
    }

    @Override
    public List<AssignmentDTO> getAllAssignmentsForCourse(String courseName) {
        if (courseRepository.findByNameIgnoreCase(courseName).isEmpty()) throw new CourseNotFoundException();
        return assignmentRepository.findAllByCourse_Name(courseName)
                .stream()
                .map(assignment -> {
                    AssignmentDTO assignmentDTO = modelMapper.map(assignment, AssignmentDTO.class);
                    String base64 = "data:image/png;base64," + Base64.getMimeEncoder().encodeToString(assignment.getContent());
                    assignmentDTO.setContent(base64);
                    return assignmentDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentAssignmentDTO> getAllAssignmentsForCourseAndForStudent(String courseName, String studentId) {
        Optional<Course> course = courseRepository.findByNameIgnoreCase(courseName);
        Optional<Student> student = studentRepository.findByIdIgnoreCase(studentId);
        if (course.isEmpty()) throw new CourseNotFoundException();
        if (student.isEmpty()) throw new StudentNotFoundException();

        return paperRepository.findAllByAssignment_Course_NameAndStudent_Id(courseName, studentId)
                .stream()
                .map(paper -> {
                    StudentAssignmentDTO studentAssignmentDTO = modelMapper.map(paper.getAssignment(), StudentAssignmentDTO.class);
                    String base64 = "data:image/png;base64," + Base64.getMimeEncoder().encodeToString(paper.getAssignment().getContent());
                    studentAssignmentDTO.setContent(base64);
                    studentAssignmentDTO.setStatus(paper.getStatus());
                    return studentAssignmentDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PaperDTO> getAllPaperForCourseAndForStudent(String courseName, String studentId) {
        Optional<Course> course = courseRepository.findByNameIgnoreCase(courseName);
        Optional<Student> student = studentRepository.findByIdIgnoreCase(studentId);
        if (course.isEmpty()) throw new CourseNotFoundException();
        if (student.isEmpty()) throw new StudentNotFoundException();

        return paperRepository.findAllByAssignment_Course_NameAndStudent_Id(courseName, studentId)
                .stream()
                .map(paper -> modelMapper.map(paper, PaperDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaperDTO> getAllPapersForAssignment(Long assignmentId) {
        if (assignmentRepository.findById(assignmentId).isEmpty()) throw new AssignmentNotFoundException();

        return paperRepository.findAllByAssignment_Id(assignmentId)
                .stream()
                .map(paper -> modelMapper.map(paper, PaperDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaperSnapshotDTO> getAllPaperSnapshotsForPaper(Long paperId) {
        if (!paperRepository.existsById(paperId)) throw new PaperNotFoundException();

        List<PaperSnapshotDTO> paperSnapshotDTOS = paperSnapshotRepository.findAllByPaper_Id(paperId)
                .stream()
                .map(paperSnapshot -> {
                    PaperSnapshotDTO paperSnapshotDTO = modelMapper.map(paperSnapshot, PaperSnapshotDTO.class);
                    String base64 = "data:image/png;base64," + Base64.getMimeEncoder().encodeToString(paperSnapshot.getContent());
                    paperSnapshotDTO.setContent(base64);
                    return paperSnapshotDTO;
                })
                .collect(Collectors.toList());

        return paperSnapshotDTOS;
    }

    @Override
    public PaperDTO updatePaperStatus(Long assignmentId, String studentId, String status) {
        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
        Optional<Student> student = studentRepository.findById(studentId);
        if(assignment.isEmpty()) throw new AssignmentNotFoundException();
        if(student.isEmpty()) throw new StudentNotFoundException();

        Paper paper = paperRepository.findByAssignment_IdAndStudent_Id(assignmentId, studentId);
        paper.setStatus(status);
        paperRepository.save(paper);

        System.out.println("Update paper:" + paper.toString());

        return modelMapper.map(paper, PaperDTO.class);
    }

    @Override
    public AssignmentDTO addAssignmentToCourse(String courseName, AssignmentDTO assignmentDTO) {

        Optional<Course> course = courseRepository.findByNameIgnoreCase(courseName);
        if (!course.isPresent()) throw new CourseNotFoundException();

        /* Create assignment */
        byte[] bytes = Base64.getMimeDecoder().decode(assignmentDTO.getContent().split(",")[1]);
        Assignment assignment = modelMapper.map(assignmentDTO, Assignment.class);
        assignment.setContent(bytes);
        assignment.setCourse(course.get());

        /* save */
        assignmentRepository.save(assignment);

        System.out.println("ciao");

        /* Create papers for every student in course */
        course.get().getStudents().forEach(student -> {
            Paper paper = Paper.builder()
                    .vote(null)
                    .lastUpdateTime(assignment.getReleaseDate())
                    .status("null")
                    .build();
            paper.setAssignment(assignment);
            paper.setStudent(student);

            paperRepository.save(paper);
        });

        return assignmentDTO;
    }

    @Override
    public PaperSnapshotDTO addPaperSnapshotToPaper(Long paperId, PaperSnapshotDTO paperSnapshotDTO, boolean toReview, Integer vote) {
        if (!paperRepository.existsById(paperId)) throw new PaperNotFoundException();

        byte[] bytes = Base64.getMimeDecoder().decode(paperSnapshotDTO.getContent().split(",")[1]);

        PaperSnapshot paperSnapshot = modelMapper.map(paperSnapshotDTO, PaperSnapshot.class);
        Paper paper = paperRepository.findById(paperId).get();
        paperSnapshot.setPaper(paper);
        paperSnapshot.setContent(bytes);

        paperSnapshotRepository.save(paperSnapshot);
        return paperSnapshotDTO;
    }

    @Override
    public List<TeamDTO> getPendingTeamsForStudent(String studentId) {
        if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException();

        List<TokenTeam> tokens = tokenRepository.findAllByStudentId(studentId);

        List<TeamDTO> tmp = null;

        tokens.forEach(teamToken ->
                tmp.add(modelMapper.map(teamToken.getTeam(), TeamDTO.class))
        );
        return tmp;
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

    /* VMs */

    @Override
    public List<VmInstanceDTO> getVmInstances(String studentId, String team) {
        if (teamRepository.getByName(team) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, team);
        if (!teamRepository.getByName(team).getMembers().contains(studentRepository.getOne(studentId)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, studentId);

        return vmInstanceRepository.getVmInstancesByTeam(teamRepository.getByName(team)).stream()
                .map(vi -> modelMapper.map(vi, VmInstanceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VmInstanceDTO createVmInstance(String studentId, String team, VmInstanceDTO vmInstance) {
        if (teamRepository.getByName(team) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, team);
        if (!teamRepository.getByName(team).getMembers().contains(studentRepository.getOne(studentId)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, studentId);
        if (vmInstanceRepository.getVmInstancesByTeam(teamRepository.getByName(team)).size() + 1 > vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()).getMaxVms())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MaxVmsInstances");
        if (vmInstance.getCountDisks() > vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()).getMaxDisk() || vmInstance.getCountDisks() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CountDisks");
        if (vmInstance.getCountRam() > vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()).getMaxRam() || vmInstance.getCountRam() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CountRam");
        if (vmInstance.getCountVcpus() > vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()).getMaxVcpus() || vmInstance.getCountVcpus() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CountVcpus");
        if (vmInstance.getOwner() != null &&
                (!studentRepository.existsById(vmInstance.getOwner()) || !vmInstance.getOwner().equals(studentId)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, vmInstance.getOwner());

        VmInstance newVmInstance = modelMapper.map(vmInstance, VmInstance.class);
        newVmInstance.setCreator(studentId);
        newVmInstance.setTeam(teamRepository.getByName(team));
        newVmInstance.setImage(vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()).getImage()); //TODO:link here
        newVmInstance.setVmModel(vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()));
        return modelMapper.map(vmInstanceRepository.save(newVmInstance), VmInstanceDTO.class);
    }

    @Override
    public VmInstanceDTO startVmInstance(String id, String team, Long idVmInstance) {
        if (teamRepository.getByName(team) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, team);
        if (!vmInstanceRepository.existsById(idVmInstance))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, idVmInstance.toString());
        if (!teamRepository.getByName(team).getMembers().contains(studentRepository.getOne(id)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, id);
        if (!vmInstanceRepository.getOne(idVmInstance).getTeam().getName().equals(team))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, idVmInstance.toString());
        if (vmInstanceRepository.getOne(idVmInstance).getOwner() != null && !vmInstanceRepository.getOne(idVmInstance).getOwner().equals(id))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, id);
        if (vmInstanceRepository.countDistinctByTeamAndStateEquals(teamRepository.getByName(team), 1) + 1 >
                vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()).getMaxRunningVms())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MaxRunningVms");

        if (vmInstanceRepository.getOne(idVmInstance).getState() != 1)
            vmInstanceRepository.getOne(idVmInstance).setState(1);
        return modelMapper.map(vmInstanceRepository.getOne(idVmInstance), VmInstanceDTO.class);
    }

    @Override
    public VmInstanceDTO stopVmInstance(String id, String team, Long idVmInstance) {
        if (teamRepository.getByName(team) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, team);
        if (!vmInstanceRepository.existsById(idVmInstance))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, idVmInstance.toString());
        if (!teamRepository.getByName(team).getMembers().contains(studentRepository.getOne(id)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, id);
        if (!vmInstanceRepository.getOne(idVmInstance).getTeam().getName().equals(team))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, idVmInstance.toString());
        if (vmInstanceRepository.getOne(idVmInstance).getOwner() != null && !vmInstanceRepository.getOne(idVmInstance).getOwner().equals(id))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, id);

        if (vmInstanceRepository.getOne(idVmInstance).getState() != 0)
            vmInstanceRepository.getOne(idVmInstance).setState(0);
        return modelMapper.map(vmInstanceRepository.getOne(idVmInstance), VmInstanceDTO.class);
    }

    @Override
    public List<VmInstanceDTO> deleteVmInstance(String id, String team, Long idVmInstance) {
        if (teamRepository.getByName(team) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, team);
        if (!vmInstanceRepository.existsById(idVmInstance))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, idVmInstance.toString());
        if (!teamRepository.getByName(team).getMembers().contains(studentRepository.getOne(id)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, id);
        if (!vmInstanceRepository.getOne(idVmInstance).getTeam().getName().equals(team))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, idVmInstance.toString());
        if (vmInstanceRepository.getOne(idVmInstance).getOwner() != null && !vmInstanceRepository.getOne(idVmInstance).getOwner().equals(id))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, id);

        vmInstanceRepository.delete(vmInstanceRepository.getOne(idVmInstance));
        return vmInstanceRepository.getVmInstancesByTeam(teamRepository.getByName(team)).stream()
                .map(vi -> modelMapper.map(vi, VmInstanceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VmInstanceDTO editVmInstance(String id, String team, Long idVmInstance, VmInstanceDTO vmInstance) {
        if (teamRepository.getByName(team) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, team);
        if (!vmInstanceRepository.existsById(idVmInstance))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, idVmInstance.toString());
        if (!teamRepository.getByName(team).getMembers().contains(studentRepository.getOne(id)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, id);
        if (!vmInstanceRepository.getOne(idVmInstance).getTeam().getName().equals(team))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, idVmInstance.toString());
        if (vmInstanceRepository.getOne(idVmInstance).getOwner() != null && !vmInstanceRepository.getOne(idVmInstance).getOwner().equals(id))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, id);
        if (vmInstanceRepository.getOne(idVmInstance).getState() != 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VmRunning");
        if (vmInstance.getCountDisks() > vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()).getMaxDisk() || vmInstance.getCountDisks() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CountDisks");
        if (vmInstance.getCountRam() > vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()).getMaxRam() || vmInstance.getCountRam() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CountRam");
        if (vmInstance.getCountVcpus() > vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()).getMaxVcpus() || vmInstance.getCountVcpus() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CountVcpus");

        vmInstanceRepository.getOne(idVmInstance).setCountDisks(vmInstance.getCountDisks());
        vmInstanceRepository.getOne(idVmInstance).setCountRam(vmInstance.getCountRam());
        vmInstanceRepository.getOne(idVmInstance).setCountVcpus(vmInstance.getCountVcpus());
        return modelMapper.map(vmInstanceRepository.getOne(idVmInstance), VmInstanceDTO.class);
    }

    @Override
    public List<CourseDTO> getAllTeacherCourses(String userId) {
        return courseRepository.findAllByTeacher(userId)
                .stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VmModelDTO getVmModel(String courseName) {
        if (courseRepository.findByNameIgnoreCase(courseName).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, courseName);

        return modelMapper.map(vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(courseName).get()), VmModelDTO.class);
    }

    @Override
    public VmModelDTO getVmModel(String id, String team) {
        if (teamRepository.getByName(team) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, team);
        if (!teamRepository.getByName(team).getMembers().contains(studentRepository.getOne(id)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, id);

        return modelMapper.map(vmModelRepository.getByCourse(teamRepository.getByName(team).getCourse()), VmModelDTO.class);
    }

    @Override
    public List<VmInstanceDTO> getVmInstancesPerCourse(String course) {
        if (courseRepository.findByNameIgnoreCase(course).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, course);

        return vmInstanceRepository.getVmInstancesByCourse(courseRepository.findByNameIgnoreCase(course).get()).stream()
                .map(vmi -> modelMapper.map(vmi, VmInstanceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VmModelDTO editVmModel(String course, VmModelDTO vmModel) {
        if (courseRepository.findByNameIgnoreCase(course).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, course);

        if (vmModel.getMaxVms() != vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxVms()) {
            if (vmModel.getMaxVms() > vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxVms())
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxVms(vmModel.getMaxVms());
            else {
                if (vmInstanceRepository.getMaxVmsPerTeam() > vmModel.getMaxVms())
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxVms");
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxVms(vmModel.getMaxVms());
            }
        }

        if (vmModel.getMaxRunningVms() != vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxRunningVms()) {
            if (vmModel.getMaxRunningVms() > vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxRunningVms())
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxRunningVms(vmModel.getMaxRunningVms());
            else {
                if (vmInstanceRepository.getMaxVmsRunningPerTeam() > vmModel.getMaxRunningVms())
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxRunningVms");
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxRunningVms(vmModel.getMaxRunningVms());
            }
        }

        if (vmModel.getMaxVcpus() != vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxVcpus()) {
            if (vmModel.getMaxVcpus() > vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxVcpus())
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxVcpus(vmModel.getMaxVcpus());
            else {
                if (vmInstanceRepository.getMaxVcpusPerTeam() > vmModel.getMaxVcpus())
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxVcpus");
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxVcpus(vmModel.getMaxVcpus());
            }
        }

        if (vmModel.getMaxRam() != vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxRam()) {
            if (vmModel.getMaxRam() > vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxRam())
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxRam(vmModel.getMaxRam());
            else {
                if (vmInstanceRepository.getMaxRamPerTeam() > vmModel.getMaxRam())
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxRam");
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxRam(vmModel.getMaxRam());
            }
        }

        if (vmModel.getMaxDisk() != vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxDisk()) {
            if (vmModel.getMaxDisk() > vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).getMaxDisk())
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxDisk(vmModel.getMaxDisk());
            else {
                if (vmInstanceRepository.getMaxDiskPerTeam() > vmModel.getMaxDisk())
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxDisk");
                vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()).setMaxDisk(vmModel.getMaxDisk());
            }
        }

        return modelMapper.map(vmModelRepository.getByCourse(courseRepository.findByNameIgnoreCase(course).get()), VmModelDTO.class);
    }

}
