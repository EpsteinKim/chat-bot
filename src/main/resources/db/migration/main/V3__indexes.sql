create index idx_chats_thread_id_created_at on chats (thread_id, created_at);
create index idx_chats_created_at on chats (created_at);

create index idx_threads_user_id_updated_at on threads (user_id, updated_at);
create index idx_threads_user_id_created_at on threads (user_id, created_at);
create index idx_threads_created_at on threads (created_at);

create index idx_feedbacks_user_id_created_at on feedbacks (user_id, created_at);
create index idx_feedbacks_created_at on feedbacks (created_at);

create index idx_activity_logs_type_created_at on activity_logs (type, created_at);
create index idx_activity_logs_user_id_created_at on activity_logs (user_id, created_at);
