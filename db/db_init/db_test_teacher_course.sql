create table teacher_course
(
    teacher_id varchar(255) not null,
    course_id  bigint       not null,
    constraint FKaleldsg7yww5as540ld8iwghe
        foreign key (teacher_id) references teacher (id),
    constraint FKp8bco6842vkqh13y4759ib7tk
        foreign key (course_id) references course (id)
);

INSERT INTO db_test.teacher_course (teacher_id, course_id) VALUES ('admin', 2);
INSERT INTO db_test.teacher_course (teacher_id, course_id) VALUES ('admin', 4);
INSERT INTO db_test.teacher_course (teacher_id, course_id) VALUES ('admin', 6);
INSERT INTO db_test.teacher_course (teacher_id, course_id) VALUES ('admin', 8);
INSERT INTO db_test.teacher_course (teacher_id, course_id) VALUES ('d123456', 2);
INSERT INTO db_test.teacher_course (teacher_id, course_id) VALUES ('d123456', 4);
INSERT INTO db_test.teacher_course (teacher_id, course_id) VALUES ('d123456', 6);
INSERT INTO db_test.teacher_course (teacher_id, course_id) VALUES ('d123456', 8);