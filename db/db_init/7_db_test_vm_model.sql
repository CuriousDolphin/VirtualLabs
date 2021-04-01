create table vm_model
(
    id              bigint       not null
        primary key,
    image           varchar(255) null,
    max_disk        int          not null,
    max_ram         int          not null,
    max_running_vms int          not null,
    max_vcpus       int          not null,
    max_vms         int          not null,
    course_id       bigint       null,
    constraint FK5uu53eh82g6d0c91tawvvlb2d
        foreign key (course_id) references course (id)
);

INSERT INTO db_test.vm_model (id, image, max_disk, max_ram, max_running_vms, max_vcpus, max_vms, course_id) VALUES (1, 'defaultVmImage.png', 3000, 48, 3, 30, 6, 2);
INSERT INTO db_test.vm_model (id, image, max_disk, max_ram, max_running_vms, max_vcpus, max_vms, course_id) VALUES (3, 'defaultVmImage.png', 3000, 48, 3, 30, 6, 4);
INSERT INTO db_test.vm_model (id, image, max_disk, max_ram, max_running_vms, max_vcpus, max_vms, course_id) VALUES (5, 'defaultVmImage.png', 3000, 48, 3, 30, 6, 6);
INSERT INTO db_test.vm_model (id, image, max_disk, max_ram, max_running_vms, max_vcpus, max_vms, course_id) VALUES (7, 'defaultVmImage.png', 3000, 48, 3, 30, 6, 8);