insert into authors (name, country)
values ('Jack London', 'USA'),
       ('Erich Maria Remarque', 'Germany')
;

insert into genres (title)
values ('Novel'),
       ('Drama')
;

insert into books (title, author_id, genre_id)
values ('Martin Eden', 1, 1),
       ('Three Comrades', 2, 1),
       ('Sea Wolf', 1, 1)
;

insert into comments (text, commenter, book_id)
values ('10/10, pishi esche', 'Commenter 1', 1),
       ('5/10', 'Commenter 2', 2),
       ('Good', 'Commenter 3', 3),
       ('Super', 'Commenter 3', 2),
       ('Very nice', 'Commenter 3', 1)
