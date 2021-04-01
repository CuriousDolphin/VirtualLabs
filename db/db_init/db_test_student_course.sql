create table student_course
(
    student_id varchar(255) not null,
    course_id  bigint       not null,
    constraint FKejrkh4gv8iqgmspsanaji90ws
        foreign key (course_id) references course (id),
    constraint FKq7yw2wg9wlt2cnj480hcdn6dq
        foreign key (student_id) references student (id)
);

INSERT INTO db_test.student_course (student_id, course_id) VALUES ('s263138', 2);
INSERT INTO db_test.student_course (student_id, course_id) VALUES ('s265542', 2);
INSERT INTO db_test.student_course (student_id, course_id) VALUES ('s263094', 2);
INSERT INTO db_test.student_course (student_id, course_id) VALUES ('s255300', 2);