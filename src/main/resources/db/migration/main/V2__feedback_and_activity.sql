create table feedbacks (
    id varchar(255) not null,
    user_id varchar(255) not null,
    chat_id varchar(255) not null,
    is_positive boolean not null,
    status varchar(255) not null,
    created_at timestamp(6) with time zone not null,
    primary key (id),
    constraint uk_feedbacks_user_chat unique (user_id, chat_id)
);

create table activity_logs (
    id varchar(255) not null,
    user_id varchar(255) not null,
    type varchar(255) not null,
    created_at timestamp(6) with time zone not null,
    primary key (id)
);
