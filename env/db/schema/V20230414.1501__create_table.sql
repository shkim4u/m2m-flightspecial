create table if not exists travelbuddy.flightspecial
(
    id                              int8 GENERATED ALWAYS AS IDENTITY,
    header                          varchar(255) not null,
    body                            varchar(255),
    origin                          varchar(255),
    origin_code                     varchar(6),
    destination                     varchar(255),
    destination_code                varchar(6),
    cost                            int4,
    expiry_date                     TIMESTAMP,
    primary key (id)
);

create table if not exists travelbuddy.flight
(
    flight_no                       int8 GENERATED ALWAYS AS IDENTITY,
    profile_id                      varchar(255),
    flight_name                     varchar(255) not null,
    pushing_status_code             varchar(255) not null,
    poping_step                     int4         not null,
    register_id                     varchar(255) not null,
    registration_date_time          TIMESTAMP    not null,
    primary key (flight_no)
);

create table if not exists travelbuddy.flight_name_history
(
    flight_name_history_no          int8 GENERATED ALWAYS AS IDENTITY,
    flight_name                     varchar(255) not null,
    flight_no                       int8         not null,
    primary key (flight_name_history_no)
);

alter table travelbuddy.flight_name_history
    add constraint flight_name_history_fk_flight_no foreign key (flight_no) references travelbuddy.flight;
