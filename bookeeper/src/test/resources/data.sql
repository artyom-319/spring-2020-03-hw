insert into authors (name, country)
values ('Jack London', 'USA'),
       ('Erich Maria Remarque', 'Germany')
;

insert into genres (title)
values ('Novel'),
       ('Drama')
;

insert into books (title, author_id, genre_id)
values ('Martin Iden', 1, 1),
       ('Drei Komraden', 2, 1),
       ('Sea Wolf', 1, 1)
;
