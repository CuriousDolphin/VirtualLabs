create table vm_instance
(
    id          bigint       not null
        primary key,
    count_disks int          not null,
    count_ram   int          not null,
    count_vcpus int          not null,
    creator     varchar(255) null,
    image       varchar(255) null,
    owner       varchar(255) null,
    state       int          not null,
    team_id     bigint       null,
    vm_model_id bigint       null,
    constraint FKf869taqr5clhnxu4vm3k3cqme
        foreign key (vm_model_id) references vm_model (id),
    constraint FKsshipikq3ycmh266mq0cj9n4i
        foreign key (team_id) references team (id)
);

INSERT INTO db_test.vm_instance (id, count_disks, count_ram, count_vcpus, creator, image, owner, state, team_id, vm_model_id) VALUES (18, 500, 8, 5, 's263138', 'defaultVmImage.png', 's263138', 1, 9, 1);
INSERT INTO db_test.vm_instance (id, count_disks, count_ram, count_vcpus, creator, image, owner, state, team_id, vm_model_id) VALUES (19, 500, 2, 1, 's263138', 'defaultVmImage.png', null, 1, 9, 1);
INSERT INTO db_test.vm_instance (id, count_disks, count_ram, count_vcpus, creator, image, owner, state, team_id, vm_model_id) VALUES (20, 500, 10, 24, 's263138', 'defaultVmImage.png', null, 0, 9, 1);