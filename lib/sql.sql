show databases;

create database pmdb character set = 'utf8';

use pmdb;

show tables;

/*
=================================================
table người dùng
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
table nhà cung cấp
*/
CREATE TABLE supliers
( 
 id bigint unsigned not null auto_increment,
 name varchar(250) not null,
 address varchar(250) not null,
 email varchar(250),
 deputy varchar(250),
 phone varchar(250),
 fax varchar(250),
 code varchar(10),
 primary key (id),
 unique key (code)
);

drop table supliers;

insert into supliers (name, address, email, deputy, phone, fax, code)
values (N'Công Ty TNHH SX-TM Tân Thuận Thành', N'Lô 43A, Đường số 2, KCN Tân Tạo, Quận Bình Tân, TP HCM', 'tanthuanthanh1@gmail.com', N'Mr. Trung', '0933 202 188', '', 'TTT'),
	   (N'Công Ty TNHH SX-TM Duy Nhật', N'Lô O, KCN An Nghiệp, X. An Hiệp, H. Châu Thành, T. Sóc Trăng', 'tam@baobiduynhat.com.vn', N'Mr. Tâm', '0793 622 857', '0793 825 964', 'DN'),
	   (N'Công Ty TNHH Một Thành Viên Trung Tre', '108/49 KP.5, P. Định Hoà, Thủ Dầu Một, T. Bình Dương', 'kinhdoanh@trungtre.com', 'Mr. Trung', '0650 3 884 700', '0650 3 884580', 'TTR'),
	   (N'Công Ty TNHH Hải Nam - CN Cần Thơ', 'K35, Đường số 3, Khu ĐTM Hưng Phú, P. Hưng Thạnh, Q. Cái Răng, Tp. Cần Thơ', '', 'Mr. Giang', '02923 733 399', '02923 753 399', 'HN');
	   
select * from supliers;

/*
=================================================
table loại bao bì
*/
create table types (
	id bigint unsigned not null auto_increment,
	name varchar(100) not null,
	unit varchar(50) not null,
	primary key (id),
	unique key (name)
);

drop table types;

insert into types (name, unit)
values (N'Thùng Carton', 'Cái'),
	   (N'Hộp Duplex', 'Cái'),
	   (N'Túi PA trắng', 'Cái'),
	   (N'Que tre', 'Que'),
	   (N'Rider', 'Tờ');
	   
select * from types;

/*
=================================================
table bao bì
*/
create table packaging(
	id bigint unsigned not null auto_increment,
	name varchar(250) not null,
	specifications varchar(250) not null,
	dimension varchar(250) not null,
	suplier bigint unsigned,
	type bigint unsigned,
	minimum_order int not null default 0,
	stamped bit not null default 0,
	code varchar(100),
	main bit not null default 0,
	note longtext,
	price float,
	primary key(id),
	unique key(name, code),
	foreign key (suplier) references supliers(id),
	foreign key (type) references types(id)
);

drop table packaging;

insert into packaging (name, specifications, dimension, suplier, type, minimum_order, stamped, code, main, note, price)
values ('Túi PA trắng 20 x 33 rider 7.5', 'PAPE, dày 100mic, hàn biên 1cm, đuôi rider 7.5 cm (bao gồm đường hàn)', '20 x 33', 4, 3, 0, 0, '', 0, '', 752),
	   (N'Que tre', 'Dài 20.5 cm, cờ 3.5 cm, đuờng kính 2.5 mm, canh trên bo tròn', '20.5 - 3.5', 3, 4, 0, 0, '', 0, '', 239),
	   (N'Rider KAILISBROS Cooked Prawn SGM 250g (New Ingredient)', 'Giấy C230, in Offset, 02 mặt khác nhau', '17.5 x 5', 1, 5, 0, 0, 'N742', 0, '', 170),
	   ('Thùng KAILISBROS Cooked Prawn SGM 250g x 20 (New COO)', 'Thùng giấy carton, 05 lớp, sóng EB, chống thấm 02 mặt, in Flexo 01 màu', '36.5 x 22 x 14', 1, 1, 0, 0, 'T556', 1, '', 6300);

select * from packaging;

/*
=================================================
table khách hàng
*/

create table customers (
	id bigint unsigned not null auto_increment,
	name varchar(250) not null,
	note longtext,
	primary key(id),
	unique key (name)
);

drop table customers;

insert into customers (name, note)
values ('KB Seafood Company PTY LTD', '');

select * from customers;

/*
=================================================
table năm sản xuất
*/
create table years(
	id bigint unsigned not null auto_increment,
	year varchar(4),
	primary key(id),
	unique key(year)
);

drop table years;

insert into years(year)
values ('2020'), ('2021');

select * from years;

/*
=================================================
table lệnh sản xuất
*/
create table commands (
	id bigint unsigned not null auto_increment,
	name varchar(250) not null,
	lot_number varchar(100),
	po_number varchar(100),
	year bigint unsigned,
	customer_id bigint unsigned,
	send_date date not null,
	shipping_date date,
	destination varchar(250),
	note longtext,
	primary key (id),
	foreign key (year) references years(id),
	foreign key (customer_id) references customers(id)
);

drop tables commands;

insert into commands (name, lot_number, po_number, year, customer_id, send_date, shipping_date, destination, note)
values ('LSX 301', '125', '', 1, 1, '2021-05-14', '2021-07-10', 'Sydney, Úc', '' );

select * from commands;

/*
=================================================
table size tôm
*/
create table sizes(
	id bigint unsigned not null auto_increment,
	size varchar(100) not null,
	primary key(id),
	unique key (size)
);

drop table sizes;

insert into sizes(size)
values ('Không phân biệt'), ('61/70'), ('16/20'), ('31/40');

select * from sizes;

/*
=================================================
table size tôm bao bì
*/
create table size_packaging (
	id bigint unsigned not null auto_increment,
	size_id bigint unsigned,
	packaging_id bigint unsigned,
	primary key(id),
	foreign key (size_id) references sizes(id),
	foreign key (packaging_id) references packaging(id)
);

drop table size_packaging;

/*
=================================================
table mặt hàng
*/
create table items(
	id bigint unsigned not null auto_increment,
	name varchar(250) not null,
	description longtext not null,
	specification varchar(250) not null,
	note longtext,
	command_id bigint unsigned,
	primary key (id),
	foreign key (command_id) references commands(id)
);

drop table items;

insert into items (name, description, specification, note, command_id)
values ('Tôm Thẻ CPD Xiên Que Tỏi 250g x 20 - SGM', 'Tôm Vannamei PD Xiên Que, Tẩm Marinade Tỏi, Luộc', '72% tôm : 28% marinade', '', 1);

select * from items;

/*
=================================================
table mặt hàng và size
*/
create table item_packaging_size (
	id bigint unsigned not null auto_increment,
	item bigint unsigned,
	size bigint unsigned,
	packaging bigint unsigned,
	quantity int not null,
	odd int not null default 0,
	stock int not null default 0,
	minimum int not null default 0,
	primary key (id),
	foreign key (item) references items(id),
	foreign key (size) references sizes(id),
	foreign key (packaging) references packaging(id)
);

drop table item_packaging_size;

select * from item_packaging_size;

insert into item_packaging_size (item, size, packaging, quantity, odd, stock, minimum)
values (1, 2, 1, 20, 150, 0, 0), (1, 2, 2, 100, 200, 0, 0), (1, 2, 3, 20, 100, 0 ,0), (1, 2, 4, 1, 10, 0, 0);

select * from item_packaging_size;
/*
=================================================
table số lượng đặt
*/
create table quantity(
	id bigint unsigned not null auto_increment,
	qty int not null default 0,
	item_id bigint unsigned,
	command_id bigint unsigned,
	primary key (id),
	foreign key (item_id) references items(id),
	foreign key (command_id) references commands(id)
);

drop table quantity;

insert into quantity (qty, item_id, command_id)
values (105, 1, 1);

select * from quantity;