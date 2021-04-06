create table team_student
(
    team_id    bigint       not null,
    student_id varchar(255) not null,
    constraint FKcikvw8vwdt6jmeyksh25q60q
        foreign key (student_id) references student (id),
    constraint FKin4tsinuxmguuh6qvtue7oyti
        foreign key (team_id) references team (id)
);

INSERT INTO db_test.team_student (team_id, student_id) VALUES (9, 's265542');
INSERT INTO db_test.team_student (team_id, student_id) VALUES (9, 's263138');
INSERT INTO db_test.team_student (team_id, student_id) VALUES (10, 's263094');
INSERT INTO db_test.team_student (team_id, student_id) VALUES (10, 's255300');