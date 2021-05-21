show databases;

create database pmdb character set = 'utf8mb4';

use pmdb;

CREATE TABLE user
( 
 id BIGINT,
 name varchar(250) NOT NULL,
 account varchar(250) NOT NULL,
 password varchar(250),
 role_id BIGINT
);

select * from user;

insert into user 
values (1, 'Admin', 'admin', 'secret', 1),
	   (2, 'User', 'user', 'secret', 2);