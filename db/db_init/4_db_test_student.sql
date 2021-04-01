create table student
(
    id        varchar(255) not null
        primary key,
    email     varchar(255) null,
    last_name varchar(255) null,
    name      varchar(255) null
);

INSERT INTO db_test.student (id, email, last_name, name) VALUES ('s255300', 's255300@studenti.polito.it', 'Verducci', 'Jacopo');
INSERT INTO db_test.student (id, email, last_name, name) VALUES ('s263094', 's263094@studenti.polito.it', 'Soncin', 'Simone');
INSERT INTO db_test.student (id, email, last_name, name) VALUES ('s263138', 's263138@studenti.polito.it', 'Murabito', 'Ivan');
INSERT INTO db_test.student (id, email, last_name, name) VALUES ('s265542', 's265542@studenti.polito.it', 'Stoisa', 'Matteo');