create table teacher
(
    id        varchar(255) not null
        primary key,
    email     varchar(255) null,
    last_name varchar(255) null,
    name      varchar(255) null
);

INSERT INTO db_test.teacher (id, email, last_name, name) VALUES ('admin', 'admin@polito.it', 'admin', 'admin');
INSERT INTO db_test.teacher (id, email, last_name, name) VALUES ('d123456', 'd123456@polito.it', 'Docet', 'Gianfranco');