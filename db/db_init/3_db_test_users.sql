create table users
(
    id       varchar(255) not null
        primary key,
    enabled  bit          not null,
    password varchar(255) null,
    username varchar(255) null,
    constraint UK_r43af9ap4edm43mmtq01oddj6
        unique (username),
    constraint UKr43af9ap4edm43mmtq01oddj6
        unique (username)
);

INSERT INTO db_test.users (id, enabled, password, username) VALUES ('admin', true, '$2a$10$1DSgceMD5cK.hFX/hQIGVeCK4tvyLxJaf8FnHYhqW3.x8Za6PTC/a', 'admin@polito.it');
INSERT INTO db_test.users (id, enabled, password, username) VALUES ('d123456', true, '$2a$10$Ny9vy0LVuaZXkzayWszqGeKXwIQh/DdZi.k3xJvu.dVkNVJnkJuLa', 'd123456@polito.it');
INSERT INTO db_test.users (id, enabled, password, username) VALUES ('s255300', true, '$2a$10$ASulDeF8cxGX1msIqNqga.gAZIXdQ7UHu8MHJb9OYzEwK0d1p9OfW', 's255300@studenti.polito.it');
INSERT INTO db_test.users (id, enabled, password, username) VALUES ('s263094', true, '$2a$10$.0QM7MQ6Bh7NZcXqP4iHH.SpYE6jAaoZ8oapk.Y8ZHkDUrPomkmbi', 's263094@studenti.polito.it');
INSERT INTO db_test.users (id, enabled, password, username) VALUES ('s263138', true, '$2a$10$CmSUus6FiaTwcHiVzTsFr.x6VWn8KA1RYNWb65ppaigfGui7qdYFi', 's263138@studenti.polito.it');
INSERT INTO db_test.users (id, enabled, password, username) VALUES ('s265542', true, '$2a$10$7.IKUhTjtnmrKsnl1Ga1weezIig07jbXJnftSf/oxPYRpfx8BSCP.', 's265542@studenti.polito.it');