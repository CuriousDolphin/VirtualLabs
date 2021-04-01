create table token_team
(
    id          varchar(255) not null
        primary key,
    expiry_date datetime     null,
    student_id  varchar(255) null,
    team_id     bigint       null,
    constraint FK40rm6043q5qkp1v9w8r4cqe76
        foreign key (team_id) references team (id)
);

