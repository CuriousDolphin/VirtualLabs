create table team
(
    id        bigint       not null
        primary key,
    name      varchar(255) null,
    status    int          not null,
    course_id bigint       null,
    owner_id  varchar(255) null,
    constraint UK_g2l9qqsoeuynt4r5ofdt1x2td
        unique (name),
    constraint FK7h610055hl7idie9p2x6rqrqy
        foreign key (owner_id) references student (id),
    constraint FKrdbahenwatuua698jkpnfufta
        foreign key (course_id) references course (id)
);

INSERT INTO db_test.team (id, name, status, course_id, owner_id) VALUES (9, 'AwesomeTeam1', 1, 2, 's263138');
