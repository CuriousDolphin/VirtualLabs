create table user_roles
(
    user_id varchar(255) not null,
    roles   varchar(255) null,
    constraint FKhfh9dx7w3ubf1co1vdev94g3f
        foreign key (user_id) references users (id)
);

INSERT INTO db_test.user_roles (user_id, roles) VALUES ('s263138', 'ROLE_STUDENT');
INSERT INTO db_test.user_roles (user_id, roles) VALUES ('s265542', 'ROLE_STUDENT');
INSERT INTO db_test.user_roles (user_id, roles) VALUES ('s263094', 'ROLE_STUDENT');
INSERT INTO db_test.user_roles (user_id, roles) VALUES ('s255300', 'ROLE_STUDENT');
INSERT INTO db_test.user_roles (user_id, roles) VALUES ('d123456', 'ROLE_PROF');
INSERT INTO db_test.user_roles (user_id, roles) VALUES ('admin', 'ROLE_STUDENT');
INSERT INTO db_test.user_roles (user_id, roles) VALUES ('admin', 'ROLE_PROF');
INSERT INTO db_test.user_roles (user_id, roles) VALUES ('admin', 'ROLE_ADMIN');