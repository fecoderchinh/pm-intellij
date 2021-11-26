/*
 * Đọc thêm:
 * - Các mối quan hệ:
 *   https://medium0.com/@emekadc/how-to-implement-one-to-one-one-to-many-and-many-to-many-relationships-when-designing-a-database-9da2de684710
 * */

-- drop database pmdb;

-- show databases;

create database pmdb character set = 'utf8';

use pmdb;

-- show tables;

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

-- select * from users;

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
 fixed_ship_address bit not null default 0,
 primary key (id),
 unique key (code)
);

-- ALTER TABLE supliers ADD fixed_ship_address bit;
-- ALTER TABLE supliers MODIFY fixed_ship_address bit not null default 0;
-- ALTER TABLE supliers DROP fixed_ship_address;

--  drop table supliers;

insert into supliers (name, address, email, deputy, phone, fax, code)
values (N'Công Ty TNHH SX-TM Tân Thuận Thành', N'Lô 43A, Đường số 2, KCN Tân Tạo, Quận Bình Tân, TP HCM', 'tanthuanthanh1@gmail.com', N'Mr. Trung', '0933 202 188', '', 'TTT'),
	   (N'Công Ty TNHH SX-TM Duy Nhật', N'Lô O, KCN An Nghiệp, X. An Hiệp, H. Châu Thành, T. Sóc Trăng', 'tam@baobiduynhat.com.vn', N'Mr. Tâm', '0793 622 857', '0793 825 964', 'DN'),
	   (N'Công Ty TNHH Một Thành Viên Trung Tre', '108/49 KP.5, P. Định Hoà, Thủ Dầu Một, T. Bình Dương', 'kinhdoanh@trungtre.com', 'Mr. Trung', '0650 3 884 700', '0650 3 884580', 'TTR'),
	   (N'Công Ty TNHH Hải Nam - CN Cần Thơ', 'K35, Đường số 3, Khu ĐTM Hưng Phú, P. Hưng Thạnh, Q. Cái Răng, Tp. Cần Thơ', '', 'Mr. Giang', '02923 733 399', '02923 753 399', 'HN');
	   
-- select * from supliers;
	
/*
=================================================
table địa chỉ giao hàng
*/
  create table ship_address (
	id bigint unsigned not null auto_increment,
	name varchar(250) not null,
 	address varchar(250) not null,
 	code_address varchar(50),
	stocker varchar(150),
	stocker_phone varchar(150)
	primary key (id),
	unique key `code_address_unique` (code_address)
);

-- alter table ship_address drop fixed_ship_address;
-- alter table ship_address add unique key `code_address_unique` (code_address);

/*create table suplier_ship_address(
	id bigint unsigned not null auto_increment,
	suplier_id bigint unsigned,
	ship_address_id bigint unsigned,
	work_order_id bigint unsigned,
	product_id bigint unsigned,
	primary key (id),
	foreign key (suplier_id) references supliers(id) on delete cascade,
	foreign key (ship_address_id) references ship_address(id) on delete cascade,
	foreign key (work_order_id) references work_order(id) on delete cascade,
	foreign key (product_id) references products(id) on delete cascade
);*/

-- drop table suplier_ship_address;

-- alter table suplier_ship_address add product_id bigint unsigned;
-- alter table suplier_ship_address add foreign key (product_id) references products(id) on delete cascade;

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
	   
-- select * from types;

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
	foreign key (suplier) references supliers(id) on delete cascade,
	foreign key (type) references types(id) on delete cascade
);

-- drop table packaging;

insert into packaging (name, specifications, dimension, suplier, type, minimum_order, stamped, code, main, note, price, stock)
values ('Túi PA trắng 20 x 33 rider 7.5', 'PAPE, dày 100mic, hàn biên 1cm, đuôi rider 7.5 cm (bao gồm đường hàn)', '20 x 33', 4, 3, 0, 0, '', 0, '', 752, 0.0),
	   (N'Que tre', 'Dài 20.5 cm, cờ 3.5 cm, đuờng kính 2.5 mm, canh trên bo tròn', '20.5 - 3.5', 3, 4, 0, 0, '', 0, '', 239, 0.0),
	   (N'Rider KAILISBROS Cooked Prawn SGM 250g (New Ingredient)', 'Giấy C230, in Offset, 02 mặt khác nhau', '17.5 x 5', 1, 5, 0, 0, 'N742', 0, '', 170, 0.0),
	   ('Thùng KAILISBROS Cooked Prawn SGM 250g x 20 (New COO)', 'Thùng giấy carton, 05 lớp, sóng EB, chống thấm 02 mặt, in Flexo 01 màu', '36.5 x 22 x 14', 1, 1, 0, 0, 'T556', 1, '', 6300, 0.0);

-- select * from packaging;

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

-- select * from customers;

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
	order_date varchar(100),
	primary key (id),
	foreign key (year) references years(id) on delete cascade,
	foreign key (customer_id) references customers(id) on delete cascade
);

-- drop tables work_order;

insert into work_order (name, lot_number, po_number, year, customer_id, send_date, shipping_date, destination, note)
values 
	(replace('LSX                  301', ' ', ''), '125', '', 1, 1, str_to_date('14-05-2021','%d-%m-%Y'), str_to_date('10-07-2021','%d-%m-%Y'), 'Sydney, Úc', '' ),
	(replace('LSX         303', ' ', ''), '126', '', 2, 1, str_to_date('14-05-2021','%d-%m-%Y'), str_to_date('10-07-2021','%d-%m-%Y'), 'Sydney, Úc', '' );

-- update work_order set name='LSX 301', lot_number='125', po_number='', year='1', customer_id='1', send_date=str_to_date('14-05-2021','%d-%m-%Y'), shipping_date=str_to_date('10-07-2021','%d-%m-%Y'), destination='Sydney, Úc', note='' where id=1;

-- ALTER TABLE work_order AUTO_INCREMENT = 1;
-- select * from work_order;

/*
=================================================
table sizes
*/
create table sizes(
	id bigint unsigned not null auto_increment,
	size varchar(100) not null,
	primary key (id),
	unique key `size_unique_keys` (size)
);

-- drop table sizes;
-- ALTER TABLE sizes AUTO_INCREMENT = 1;

insert into sizes(size)
values ('61/70'), ('16/20'), ('31/40');

-- select * from sizes;

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
values 
	('Tôm Thẻ CPD Xiên Que Tỏi 250g x 20 - SGM', 'Tôm Vannamei PD Xiên Que, Tẩm Marinade Tỏi, Luộc', '72% tôm : 28% marinade', 'mô tả ngắn'),
	('Tôm Sú CPD Tẩm Tỏi Bơ - GM (Coles)', 'Tôm Vannamei PD Xiên Que, Tẩm Marinade Tỏi, Luộc', '72% tôm : 28% marinade', 'mô tả ngắn');

-- select * from products;

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
	pack_qty float not null,
	note longtext,
	primary key (id),
	foreign key (product_id) references products(id) on delete cascade,
	foreign key (size_id) references sizes(id) on delete cascade,
	foreign key (packaging_id) references packaging(id) on delete cascade
);

-- ALTER TABLE packaging_product_size MODIFY pack_qty float not null default 0;

-- ALTER TABLE packaging_product_size ADD note longtext;

-- drop table packaging_product_size;

insert into packaging_product_size (product_id, size_id, packaging_id, pack_qty)
values 
	(1, 2, 1, 20), (1, 2, 2, 100), (1, 2, 3, 20), (1, 2, 4, 1),
	(2, 3, 1, 20), (2, 3, 2, 50), (2, 3, 3, 20), (2, 3, 4, 1);

-- ALTER TABLE packaging_product_size AUTO_INCREMENT = 1;

-- select * from packaging_product_size;

/*
 * Đoạn dưới dùng cho packagingOwner 
 * (chuyển đổi dữ liệu thành string để tiện cho việc quản lý trên hệ thống)
 * */
/*
 * 
select a.id as id, b.name as packagingName, c.name as productName, d.size as size, a.pack_qty as pack_qty
from packaging_product_size a, packaging b, products c, sizes d
where a.packaging_id = b.id and a.product_id = c.id and a.size_id = d.id
order by c.name;
 * 
 * */
/*
=================================================
table mối quan hệ giữa lệnh sản xuất, đơn hàng, mặt hàng
- Một đơn hàng có thể gồm 1 hoặc nhiều lệnh sản xuất
- Một lệnh sản xuất thuộc 1 hoặc nhiều đơn hàng
- Một đơn hàng gồm 1 hoặc nhiều mặt hàng
*/
create table work_order_product(
	id bigint unsigned not null default 0,
	work_order_id bigint unsigned,
	ordinal_num varchar(100),
	product_id bigint unsigned,
	qty float not null default 0,
	note longtext,
	unique(id),
	primary key (id),
	foreign key (product_id) references products(id) on delete cascade,
	foreign key (work_order_id) references work_order(id) on delete cascade
);

-- ALTER TABLE work_order_product MODIFY ordinal_num float not null default 0;

-- drop table work_order_product;

-- select id from work_order_product order by id desc limit 1;

/*insert into work_order_product (work_order_id, ordinal_num, product_id, qty, note)
values (1, 1.0, 1, 500, "");*/

-- delete from work_order_product;

-- select * from work_order_product;

/*select wop.id as id, wo.name as workOrderName, p.name as productName, wop.ordinal_num as productOrdinalNumber, wop.qty as productQuantity, wop.note as productNote
from work_order_product wop, products p, work_order wo 
where wop.work_order_id  = wo.id and wop.product_id = p.id
order by wop.ordinal_num;*/

/*
 * work_order_product_packaging
 * -- mối quan hệ giữa LSX, Mặt hàng và Bao bì
 * */
create table work_order_product_packaging (
	id bigint unsigned not null auto_increment,
	wop_id bigint unsigned,
	work_order_id bigint unsigned,
	product_id bigint unsigned,
	packaging_id bigint unsigned,
	work_order_qty float not null default 0, -- new
	stock float not null default 0, -- new
	actual_qty float not null default 0, -- new
	residual_qty float not null default 0, -- new
	printed varchar(250),
	note longtext,
	ship_address bigint unsigned,
	primary key (id),
	foreign key (wop_id) references work_order_product(id) on delete cascade,
	foreign key (work_order_id) references work_order(id) on delete cascade,
	foreign key (product_id) references products(id) on delete cascade,
	foreign key (packaging_id) references packaging(id) on delete cascade,
	foreign key (ship_address) references ship_address(id) on delete cascade
);

-- alter table work_order_product_packaging add ship_address bigint unsigned;
-- alter table work_order_product_packaging add foreign key (ship_address) references ship_address(id) on delete cascade;

-- drop table work_order_product_packaging;

-- select * from work_order_product_packaging;

-- delete from work_order_product_packaging;

-- update work_order_product_packaging set actual_qty=2150 where wop_id=1 and packaging_id=1 and id=1;

/*SELECT wopp.product_id
FROM work_order_product_packaging wopp
WHERE wopp.work_order_id = 1
group by wopp.product_id;*/

/*
 * Các thao tác bên dưới có thể được thực hiện trong cùng 1 hành động duy nhất (Thêm, sửa xóa giữa 2 bảng work_order_product và work_order_product_packaging)
 * */

-- bước 1: ví dụ thêm số lượng thùng cần đặt cho mặt hàng
/*insert into work_order_product (work_order_id, ordinal_num, product_id, qty, note)
values (1, 1.0, 1, 150, "");*/

-- bước 2: áp dụng số lượng thùng cho nhóm bao bì thuộc mặt hàng đó
/*insert into work_order_product_packaging(wop_id, work_order_id, product_id, packaging_id, work_order_qty, stock, actual_qty, residual_qty)
select (select LAST_INSERT_ID() from work_order_product), 1, 1, pps.packaging_id, pps.pack_qty *150,0,0,0
from packaging_product_size pps
where pps.product_id = 1;*/

-- cập nhật số lượng cho từng loại bao bì khi thay đổi số lượng thùng trên
/*update work_order_product_packaging wopp
join (
	select pps.packaging_id as _id, pps.pack_qty as _pack_qty
	from packaging_product_size pps
	where pps.product_id=1
) pps on wopp.packaging_id = pps._id
join (
	select wop.id as id
	from work_order_product wop
	where wop.product_id = 1 and wop.id = 1
) wop on wopp.wop_id = wop.id
set wopp.work_order_qty = pps._pack_qty * 234;*/

-- khi xóa mặt hàng từ work_order_product, sẽ kết hợp xóa tất cả bao bì có quan hệ wop_id liên quan trong bảng work_order_product_packaging
/*delete from work_order_product_packaging where id in (
	select id
	from work_order_product_packaging wopp
	where wopp.wop_id = 2
);*/

/*
=================================================
table hiển thị thông tin số lượng nhập/xuất, có thể dùng để theo dõi/thống kê số liệu trong tương lai
- CHƯA TEST THỰC TẾ
*/

/*select distinct 
	wopp.id as woppID,
	wop.ordinal_num as woOrdinalNumbers, 
	wop.id as woID, 
	wo.name as woName, 
	p.name as productName, 
	p2.name as packName, 
	p2.specifications as packSpecs, 
	p2.dimension as packDim, 
	t.unit as packUnit, 
	s.code as sCode, 
	p2.code as packCode, 
	pps.pack_qty as packQuantity,
	pps.pack_qty * wop.qty as woQuantity,
	wopp.stock as packStock,
	wopp.actual_qty as packActualQuantity,
	wopp.residual_qty as packResidualQuantity,
	(wopp.actual_qty + wopp.stock - wopp.residual_qty - (pps.pack_qty * wop.qty)) as totalResidualQuantity,
	wopp.printed as printStatus,
	wo.order_date as orderDate
from 
	work_order_product wop,
	work_order wo,
	products p,
	packaging p2,
	types t,
	supliers s,
	packaging_product_size pps,
	work_order_product_packaging wopp 
where
	wop.work_order_id = wo.id
	and wop.product_id = p.id
	and p2.`type` = t.id
	and p2.suplier = s.id 
	and pps.product_id = p.id 
	and pps.packaging_id = p2.id
	and wopp.wop_id = wop.id
	and wopp.work_order_id = wo.id
	and wopp.product_id = p.id
	and wopp.packaging_id = p2.id
-- 	and wo.id in ("1") -- filter by work_order.id
-- 	and p.id = 1 -- filter by products.id
-- 	and wop.ordinal_num = 1 -- filter by work_order_product.ordinal_num 
;*/

/*select distinct
	(@count := @count + 1),
	wopp.id as id,
	wop.ordinal_num as ordinalNumbers, 
	wopp.wop_id as wop_id,
	wo.name as workOrderName, 
	p2.name as productName, 
	p.name as packagingName, 
	p.specifications as packagingSpecification, 
	p.dimension as packagingDimension, 
	s.code as packagingSuplier, 
	p.code as packagingCode, 
	t.unit as unit, 
	p.stamped as printStatus, 
	pps.pack_qty as packQuantity,
	(pps.pack_qty * wop.qty) as workOrderQuantity, 
-- 	wopp.work_order_qty as workOrderQuantity, -- based on above formula
	wopp.stock as stock, 
	wopp.actual_qty as actualQuantity, 
	wopp.residual_qty as residualQuantity,
	(wopp.actual_qty + wopp.stock - wopp.residual_qty - (pps.pack_qty * wop.qty)) as totalResidualQuantity,
-- 	"" as totalResidualQuantity, -- based on above formula
	wop.note as noteProduct,
	y.year as year,
	"" as privateNode,
	"" as cbm
from 
	work_order wo, 
	work_order_product wop, 
	packaging_product_size pps, 
	packaging p, 
	products p2, 
	types t,
	work_order_product_packaging wopp,
	years y,
	supliers s
CROSS JOIN (SELECT @count := 0) AS dummy
where 
	wo.id = wop.work_order_id 
	and wop.product_id = p2.id 
	and pps.product_id = p2.id 
	and pps.packaging_id = p.id 
	and p.`type` = t.id
	and wopp.work_order_id = wo.id 
	and wopp.product_id = p2.id 
-- 	and wopp.packaging_id = p.id 
	and wo.`year`  = y.id
	and p.suplier = s.id
	and wopp.work_order_id = 3 and wop.product_id = 3
order by s.code ASC;*/


/*tính số khối*/
/*select 
	p.name, 
	p.dimension, 
	(SELECT SUBSTRING_INDEX(p.dimension , 'x', 1)) as length, 
	(SELECT SUBSTRING_INDEX((SELECT SUBSTRING_INDEX(p.dimension , 'x', 2)) , 'x', -1)) as width, 
	(SELECT SUBSTRING_INDEX(p.dimension , 'x', -1)) as height,
	((SELECT SUBSTRING_INDEX(p.dimension , 'x', 1))*(SELECT SUBSTRING_INDEX((SELECT SUBSTRING_INDEX(p.dimension , 'x', 2)) , 'x', -1))*(SELECT SUBSTRING_INDEX(p.dimension , 'x', -1))*wop.qty) as CBM
from packaging p, products p2, work_order_product wop, work_order_product_packaging wopp 
where wop.product_id = p2.id and wopp.packaging_id = p.id and wop.product_id = 1 and type=1;*/

/*list đề nghị*/
/*select distinct (@count := @count + 1) AS rowNumber, wo.id as woID, wo.name as workOrderName, p.name as packagingName,  p.specifications as specs,  p.dimension as dimension, t.unit as unit, wopp.actual_qty as total, if((wopp.actual_qty - wopp.residual_qty + wopp.stock - wopp.work_order_qty) > 0, (wopp.actual_qty - wopp.residual_qty + wopp.stock - wopp.work_order_qty), "") as totalResidualQuantity
from 
	work_order_product_packaging wopp, 
	packaging p, 
	supliers s,
	years y,
	work_order wo,
	types t,
	packaging_product_size pps,
	work_order_product wop 
CROSS JOIN (SELECT @count := 0) AS dummy
where 
	wopp.packaging_id = p.id
	and wopp.work_order_id = wo.id
	and wo.`year` = y.id
	and p.suplier = s.id
	and p.`type` = t.id 
	and pps.product_id = wopp.product_id
	and wopp.wop_id = wop.id
	and wopp.work_order_id in ("1", "2")
	and wopp.actual_qty > 0
	group by wopp.id;*/
	
/*đơn đặt hàng*/
/*select
	wopp.id as woppID,
	wo.id as woID,
	group_concat(distinct wo.name separator "+") as woName, 
	p.name as pName,
	p.specifications as pSpecs,
	p.dimension as pDimension,
	t.unit as pUnit,
	sum(wopp.work_order_qty) as pDesireQuantity,
	sum(wopp.actual_qty) as pTotal,
	sum(wopp.stock) as pStock,
	sum(wopp.residual_qty) as pResidualQuantity,
	s.code as sCode,
	s.address as sAddress,
	s.deputy as sDeputy,
	s.name as sName,
	s.phone as sPhone,
	s.fax as sFax,
	sa.code_address as shipAddress
from 
	work_order_product_packaging wopp,
	work_order wo,
	packaging p,
	types t ,
	supliers s,
	ship_address sa
where 
	wopp.work_order_id = wo.id
	and wopp.packaging_id = p.id
	and p.`type` = t.id
	and p.suplier = s.id
	and wopp.ship_address = sa.id
-- 	and wopp.actual_qty > 0 
	and wopp.work_order_id in (6,7,8)
-- 	and s.code = " TTT"
-- 	and sa.code_address = "SVN"
group by 
	wopp.packaging_id;*/