create table users (
    id varchar(255) not null,
    email varchar(255) not null,
    password_hash varchar(255) not null,
    name varchar(255) not null,
    role varchar(255) not null,
    created_at timestamp(6) with time zone not null,
    primary key (id),
    constraint uk_users_email unique (email)
);

create table threads (
    id varchar(255) not null,
    user_id varchar(255) not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create table chats (
    id varchar(255) not null,
    thread_id varchar(255) not null,
    user_id varchar(255) not null,
    question text not null,
    answer text not null,
    model varchar(255) not null,
    created_at timestamp(6) with time zone not null,
    primary key (id)
);
