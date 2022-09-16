INSERT INTO users
VALUES ('admin', '$2a$10$uiHTzLvt7g1N5SfMiI3aheDRN0HYQq.WnxSVU8AqrAmKta8jDDyHq',
        'Даниил', 'Артюхин', '9777115857', 0, 'ADMIN', true),
       ('mail@mail.ru', '$2a$10$50yqPkbtdGxA28WE5NhPlu1QH.bRUY3kEUoSfAV51IwWZb6C/zUSS',
        'Михаил', 'Егоров', '9032276404', 2, 'USER', true),
       ('mail@mail.com', '$2a$10$50yqPkbtdGxA28WE5NhPlu1QH.bRUY3kEUoSfAV51IwWZb6C/zUSS',
        'Елена', 'Павлова', '9972340128', 0, 'USER', true);

INSERT INTO posts
VALUES ('mail@mail.com', 'Apple Iphone 13 256 gb', 'Абсолютно новый Айфон, оказался ненужен',
        125500.00, 1000.00, false, 2022-09-08, 'ELECTRONICS', 0),
       ('mail@mail.ru', 'Apple Iphone 11 Pro 128 gb', 'Айфон 11 про, был в использовании полтора года, аккумулятор изношен всего на 7%, физических повреждений нет',
        52000.00, 8000.00, false, 2022-09-09, 'ELECTRONICS', 2),
       ('mail@mail.ru', 'Xbox Series S 512gb', 'Младшая консоль нового поколения от Microsoft, в идеальном состоянии',
        23500.00, 0, true, 2022-09-09, 'ELECTRONICS', 2),
       ('mail@mail.ru', 'Xiaomi Redmi Buds 4 Pro', 'Неплохие TWS-ки, пользовалась полгода. Решила обновиться до яблочных.',
        2800.00, 1000.00, false, 2022-09-11, 'ELECTRONICS', 2),
       ('mail@mail.com', 'Пальто Top Man M', 'Мужское пальто размера М, в нормальном состоянии',
        4200.00, 0, false, 2022-09-12, 'CLOTHES', 0);

INSERT INTO comments
VALUES ('mail@mail.ru', 3, 'Интересное объявление, но цена завышена', '2022-09-08 14:43:33.134473');

INSERT INTO messages
VALUES ('mail@mail.ru', 'mail@mail.com',
        'Здравствуйте, хотел бы узнать у вас о возможности снижения цены на Айфон 13', '2022-09-08 14:47:13.270377'),
       ('mail@mail.com', 'mail@mail.ru',
        'Могу рассмотреть вариант снижения цены на сумму не больше 5 тысяч рублей' , '2022-09-08 14:49:02.327292');