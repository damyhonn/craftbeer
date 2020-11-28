create table beer (
    id integer not null generated always as identity (start with 1, increment by 1),
    name varchar(256) not null,
    ingredients varchar(256),
    alcohol_content varchar(256) not null,
    price double,
    category varchar(128)
);
