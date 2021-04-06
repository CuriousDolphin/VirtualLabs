create table registration_token
(
    id          varchar(255) not null
        primary key,
    expiry_date datetime     null,
    user_id     varchar(255) null,
    constraint FKpmf52fk27bywm8is5s2jv3xam
        foreign key (user_id) references users (id)
);

