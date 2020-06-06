create table authors (
    id      bigserial not null
        constraint authors_pkey primary key,
    name    varchar(512),
    country varchar(256)
);

create table genres (
    id    bigserial not null
        constraint genres_pkey primary key,
    title varchar(256)
);

create table books (
    id        bigserial not null constraint books_pkey
            primary key,
    title     varchar(1024),
    author_id bigint
        constraint books_author_fkey references authors on delete cascade ,
    genre_id  bigint
        constraint books_genre_fkey references genres on delete cascade
);