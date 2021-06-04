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
values (1, N'TĂ¢n Thuáº­n ThĂ nh', N'SĂ i GĂ²n', 'tanthuanthanh7@gmail.com', N'Mr. ToĂ n', '0906 388 337', '0906 304 336', 'TTT'),
	   (2, N'Duy Nháº­t', N'TĂ¢y Ninh', 'vantam@baobiduynhat.com', N'Mr. TĂ¢m', '0906 388 337', '0906 304 336', 'DN');
	   
select * from supliers;

/*
=================================================
table loai
*/
create table types (
	id bigint unsigned not null auto_increment,
	name varchar(100) not null,
	primary key (id)
);

insert into types (name)
values (N'Thùng Carton'),
	   (N'Hộp Carton'),
	   (N'Hộp Duplex'),
	   (N'Hộp Ivory'),
	   (N'Túi PE trắng'),
	   (N'Túi PE xanh'),
	   (N'Túi PA trắng'),
	   (N'Túi PE in'),
	   (N'Túi PA in'),
	   (N'Decal'),
	   (N'Rider'),
	   (N'Thẻ'),
	   (N'Manh PE trắng'),
	   (N'Manh PE xanh'),
	   (N'Túi PE trắng xếp hông'),
	   (N'Túi PE xanh xếp hông');
	   
select * from types;