/*
 * Đọc thêm:
 * - Các mối quan hệ:
 *   https://medium0.com/@emekadc/how-to-implement-one-to-one-one-to-many-and-many-to-many-relationships-when-designing-a-database-9da2de684710
 * */

-- drop database pmdb;

show databases;

create database pmdb character set = 'utf8';

use pmdb;

# drop database pmdb;

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

--  drop table supliers;

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

-- drop table types;

insert into types (name, unit)
values (N'Thùng Carton', 'Cái'),
	   (N'Hộp Duplex', 'Cái'),
	   (N'Túi PA trắng', 'Cái'),
	   (N'Que tre', 'Que'),
	   (N'Rider', 'Tờ');
	   
select * from types;

/*
=================================================
table mối quan hệ giữa bao bì, nhà cung cấp, loại
- Bao bì chỉ thuộc 1 loại (Túi, Thùng, Hộp, ...)
- Bao bì thuộc 1 nhà cung cấp, được phân biệt bằng code (mã bao bì mà nhà cung cấp qui định)
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
	stock float,
	primary key(id),
	unique key `packaging_unique_keys`(code, type, suplier),
	foreign key (suplier) references supliers(id),
	foreign key (type) references types(id)
);

-- drop table packaging;

insert into packaging (name, specifications, dimension, suplier, type, minimum_order, stamped, code, main, note, price, stock)
values ('Túi PA trắng 20 x 33 rider 7.5', 'PAPE, dày 100mic, hàn biên 1cm, đuôi rider 7.5 cm (bao gồm đường hàn)', '20 x 33', 4, 3, 0, 0, '', 0, '', 752, 0.0),
	   (N'Que tre', 'Dài 20.5 cm, cờ 3.5 cm, đuờng kính 2.5 mm, canh trên bo tròn', '20.5 - 3.5', 3, 4, 0, 0, '', 0, '', 239, 0.0),
	   (N'Rider KAILISBROS Cooked Prawn SGM 250g (New Ingredient)', 'Giấy C230, in Offset, 02 mặt khác nhau', '17.5 x 5', 1, 5, 0, 0, 'N742', 0, '', 170, 0.0),
	   ('Thùng KAILISBROS Cooked Prawn SGM 250g x 20 (New COO)', 'Thùng giấy carton, 05 lớp, sóng EB, chống thấm 02 mặt, in Flexo 01 màu', '36.5 x 22 x 14', 1, 1, 0, 0, 'T556', 1, '', 6300, 0.0);

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

-- drop table customers;

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

-- drop table years;

insert into years(year)
values ('2020'), ('2021');

select * from years;

/*
=================================================
table mối quan hệ giữa lệnh sản xuất, năm, khách hàng
- Số lệnh sản xuất là duy nhất theo từng năm.
- Một lệnh sản xuất chỉ thuộc về một khách hàng.
- Khách hàng có thể yêu cầu một hoặc nhiều lệnh sản xuất.
*/
create table work_order (
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
	unique key `work_order_unique_keys`(year, customer_id),
	foreign key (year) references years(id),
	foreign key (customer_id) references customers(id)
);

-- drop tables work_order;

insert into work_order (name, lot_number, po_number, year, customer_id, send_date, shipping_date, destination, note)
values ('LSX 301', '125', '', 1, 1, str_to_date('14-05-2021','%d-%m-%Y'), str_to_date('10-07-2021','%d-%m-%Y'), 'Sydney, Úc', '' );

update work_order set name='LSX 301', lot_number='125', po_number='', year='1', customer_id='1', send_date=str_to_date('14-05-2021','%d-%m-%Y'), shipping_date=str_to_date('10-07-2021','%d-%m-%Y'), destination='Sydney, Úc', note='' where id=1;

select * from work_order;

/*
=================================================
table sizes
*/
create table sizes(
	id bigint unsigned not null auto_increment,
	size varchar(100) not null,
	primary key `size_unique_keys`(id),
	unique key (size)
);

-- drop table sizes;

insert into sizes(size)
values ('61/70'), ('16/20'), ('31/40');

select * from sizes;

/*
=================================================
table mặt hàng
*/
create table products(
	id bigint unsigned not null auto_increment,
	name varchar(250) not null,
	description longtext not null,
	specification varchar(250) not null,
	note longtext,
	primary key (id)
);

-- drop table products;

insert into products (name, description, specification, note)
values ('Tôm Thẻ CPD Xiên Que Tỏi 250g x 20 - SGM', 'Tôm Vannamei PD Xiên Que, Tẩm Marinade Tỏi, Luộc', '72% tôm : 28% marinade', 'mô tả ngắn');

select * from products;

/*
=================================================
table mối quan hệ giữa bao bì, mặt hàng, size
- Một mặt hàng có 1 hoặc nhiều size
- Một size cũng thuộc 1 hoặc nhiều mặt hàng
- Một loại bao bì có thể thuộc 1 hoặc nhiều mặt hàng
- Một mặt hàng có thể có 1 hoặc nhiều loại bao bì
*/
create table packaging_product_size (
	id bigint unsigned not null auto_increment,
	product_id bigint unsigned,
	size_id bigint unsigned,
	packaging_id bigint unsigned,
	pack_qty int not null,
	primary key (id),
	foreign key (product_id) references products(id) on delete cascade,
	foreign key (size_id) references sizes(id) on delete cascade,
	foreign key (packaging_id) references packaging(id) on delete cascade
);

-- drop table packaging_product_size;

insert into packaging_product_size (product_id, size_id, packaging_id, pack_qty)
values (1, 2, 1, 20), (1, 2, 2, 100), (1, 2, 3, 20), (1, 2, 4, 1);

select * from packaging_product_size;

/*
 * Đoạn dưới dùng cho packagingOwner 
 * (chuyển đổi dữ liệu thành string để tiện cho việc quản lý trên hệ thống)
 * */
select a.id as id, b.name as packagingName, c.name as productName, d.size as size, a.pack_qty as pack_qty
from packaging_product_size a, packaging b, products c, sizes d
where a.packaging_id = b.id and a.product_id = c.id and a.size_id = d.id
order by c.name;
/*
=================================================
table mối quan hệ giữa lệnh sản xuất, đơn hàng, mặt hàng
- Một đơn hàng có thể gồm 1 hoặc nhiều lệnh sản xuất
- Một lệnh sản xuất thuộc 1 hoặc nhiều đơn hàng
- Một đơn hàng gồm 1 hoặc nhiều mặt hàng
*/
create table work_order_product(
	id bigint unsigned not null auto_increment,
	work_order_id bigint unsigned,
	ordinal_num varchar(100) not null default "#",
	product_id bigint unsigned,
	qty float not null default 0,
	note longtext,
	primary key (id),
	foreign key (product_id) references products(id) on delete cascade,
	foreign key (work_order_id) references work_order(id) on delete cascade
);

-- drop table work_order_product;

insert into work_order_product (work_order_id, ordinal_num, product_id, qty, note)
values (1, "1", 1, 500, "");

select * from work_order_product;

/*
 * work_order_product_packaging
 * */
create table work_order_product_packaging (
	id bigint unsigned not null auto_increment,
	work_order_id bigint unsigned,
	product_id bigint unsigned,
	packaging_id bigint unsigned,
	work_order_qty float not null default 0, -- new
	stock float not null default 0, -- new
	actual_qty float not null default 0, -- new
	residual_qty float not null default 0, -- new
	primary key (id),
	foreign key (work_order_id) references work_order(id) on delete cascade,
	foreign key (product_id) references products(id) on delete cascade,
	foreign key (packaging_id) references packaging(id) on delete cascade
);

-- drop table work_order_product_packaging;

/*
=================================================
table mối quan hệ giữa command_product và order (kết hợp số lượng tồn và số lượng đặt)
- CHƯA XONG
*/
select 
	wop.ordinal_num as ordinalNumbers, 
	wo.name as workOrderName, 
	p2.name as productName, 
	p.name as packagingName, 
	p.specifications as packagingSpecification, 
	p.dimension as packagingDimension, 
	p.suplier as packagingSuplier, 
	p.code as packagingCode, 
	t.unit as unit, 
	p.stamped as printStatus, 
	pps.pack_qty as packQuantity,
	(pps.pack_qty * wop.qty) as workOrderQuantity, 
	wopp.stock as Stock, 
	wopp.actual_qty as actualQuantity, 
	wopp.residual_qty as residualQuantity,
	(wopp.actual_qty - wopp.residual_qty - wopp.stock - (pps.pack_qty * wop.qty)) as totalResidualQuantity,
	wop.note as noteProduct
from 
	work_order wo, 
	work_order_product wop, 
	packaging_product_size pps, 
	packaging p, 
	products p2, 
	types t,
	work_order_product_packaging wopp
where 
	wo.id = wop.work_order_id 
	and wop.product_id = p2.id 
	and pps.product_id = p2.id 
	and pps.packaging_id = p.id 
	and p.`type` = t.id
	and wopp.work_order_id = wo.id 
	and wopp.product_id = p2.id 
	and wopp.packaging_id = p.id 
order by wop.ordinal_num;