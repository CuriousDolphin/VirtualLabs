create table paper
(
    id               bigint       not null
        primary key,
    last_update_time datetime     null,
    status           varchar(255) null,
    vote             int          null,
    assignment_id    bigint       null,
    student_id       varchar(255) null,
    constraint FKbd9atdof177up3jojhoma93j6
        foreign key (student_id) references student (id),
    constraint FKl3jkbqdov3q7imp3vjbq15qxg
        foreign key (assignment_id) references assignment (id)
);

INSERT INTO db_test.paper (id, last_update_time, status, vote, assignment_id, student_id) VALUES (12, '2021-04-01 14:55:55', 'null', null, 11, 's255300');
INSERT INTO db_test.paper (id, last_update_time, status, vote, assignment_id, student_id) VALUES (13, '2021-04-01 14:55:55', 'null', null, 11, 's263094');
INSERT INTO db_test.paper (id, last_update_time, status, vote, assignment_id, student_id) VALUES (14, '2021-04-01 14:55:55', 'reviewed', null, 11, 's263138');
INSERT INTO db_test.paper (id, last_update_time, status, vote, assignment_id, student_id) VALUES (15, '2021-04-01 14:55:55', 'null', null, 11, 's265542');