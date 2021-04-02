create table course
(
    id      bigint       not null
        primary key,
    acronym varchar(255) null,
    enabled bit          not null,
    max     int          not null,
    min     int          not null,
    name    varchar(255) null,
    constraint UK_4xqvdpkafb91tt3hsb67ga3fj
        unique (name),
    constraint UK_stk4wd70i5f8cxs1hcb27y70j
        unique (acronym)
);

INSERT INTO db_test.course (id, acronym, enabled, max, min, name) VALUES (2, 'AI', true, 5, 2, 'APPLICAZIONI INTERNET');
INSERT INTO db_test.course (id, acronym, enabled, max, min, name) VALUES (4, 'PDS', true, 4, 2, 'PROGRAMMAZIONE DI SISTEMA');
INSERT INTO db_test.course (id, acronym, enabled, max, min, name) VALUES (6, 'ML', true, 3, 1, 'MACHINE LEARNING');
INSERT INTO db_test.course (id, acronym, enabled, max, min, name) VALUES (8, 'DS', true, 5, 1, 'DATA SPACES');