create table refresh_tokens (
    id int primary key generated always as identity,
    token varchar(64) not null unique,
    user_id int not null references users(id),
    expires_at timestamp not null
);