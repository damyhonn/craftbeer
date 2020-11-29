create table beer (
    id integer primary key,
    name varchar(256) not null,
    ingredients varchar(256),
    alcohol_content varchar(256) not null,
    price double,
    category varchar(128)
);
