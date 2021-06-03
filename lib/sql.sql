show databases;

create database pmdb character set = 'utf8';

use pmdb;

show tables;

/*
=================================================
table users
*/

CREATE TABLE users
( 
 id bigint unsigned not null auto_increment,
 name varchar(250) not null,
 account varchar(250) not null,
 password varchar(250) not null,
 role_id BIGINT,
 primary key (id),
 unique key (account)
);

insert into users
values (1, 'Admin', 'admin', 'secret', 1),
	   (2, 'User', 'user', 'secret', 2);

select * from users;

/*
=================================================
table nhacungcap
*/
CREATE TABLE supliers
( 
 id bigint unsigned not null auto_increment,
 name varchar(250) collate utf8mb4_unicode_ci not null,
 address varchar(250) collate utf8mb4_unicode_ci not null,
 email varchar(250),
 deputy varchar(250) collate utf8mb4_unicode_ci,
 phone varchar(250),
 fax varchar(250),
 code varchar(10),
 primary key (id),
 unique key (code)
);

insert into supliers
values (1, N'Tân Thuận Thành', N'Sài Gòn', 'tanthuanthanh7@gmail.com', N'Mr. Toàn', '0906 388 337', '0906 304 336', 'TTT'),
	   (2, N'Duy Nhật', N'Tây Ninh', 'vantam@baobiduynhat.com', N'Mr. Tâm', '0906 388 337', '0906 304 336', 'DN');
	   
select * from supliers;