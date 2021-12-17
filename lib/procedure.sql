delimiter #
create procedure insert_wopp_children
(
	in t_wop_id bigint,
	in t_work_order_id bigint,
	in t_ordinal_num varchar(100),
	in t_product_id bigint,
	in t_qty float,
	in t_note longtext,
	in t_ship_address bigint,
	in t_order_times int
)
begin
	insert into work_order_product (id, work_order_id, ordinal_num, product_id, qty, note, order_times)
	values (t_wop_id, t_work_order_id, t_ordinal_num, t_product_id, t_qty, "", t_order_times);

	insert into work_order_product_packaging(wop_id, work_order_id, product_id, packaging_id, work_order_qty, stock, actual_qty, residual_qty, ship_address, order_times)
	select t_wop_id, t_work_order_id, t_product_id, pps.packaging_id, pps.pack_qty *t_qty,0,0,0, t_ship_address, t_order_times
	from packaging_product_size pps, work_order_product wop
	where pps.product_id = t_product_id and wop.id = t_wop_id;
end #

delimiter;
 drop PROCEDURE insert_wopp_children;
call insert_wopp_children(6, 2, 2.0, 2, 350, "", 1, 1);

delimiter #
create procedure update_wopp_children
(
	in t_wop_id bigint,
	in t_work_order_id bigint,
	in t_ordinal_num varchar(100),
	in t_product_id bigint,
	in t_qty float,
	in t_note longtext,
	in t_order_times int
)
begin
	update work_order_product set work_order_id=t_work_order_id, ordinal_num=t_ordinal_num, product_id=t_product_id, qty=t_qty, note=t_note, order_times=t_order_times where id=t_wop_id;

	update work_order_product_packaging wopp	 join (		 select pps.packaging_id as _id, pps.pack_qty as _pack_qty		 from packaging_product_size pps		 where pps.product_id=t_product_id	 ) pps on wopp.packaging_id = pps._id	 set wopp.work_order_qty = pps._pack_qty * t_qty, wopp.order_times=t_order_times where wopp.wop_id = t_wop_id;
end #

delimiter;
 drop PROCEDURE update_wopp_children;
call update_wopp_children(16, 4, 4.0, 14, 350, "", 1);

delimiter #
create procedure delete_wopp_children
(
	in t_work_order_id bigint
)
begin
	delete from work_order where id=t_work_order_id;

	delete from work_order_product_packaging where id in (
		select id
		from work_order_product_packaging wopp
		where wopp.work_order_id=t_work_order_id
	);
end #

delimiter;
 drop PROCEDURE delete_wopp_children;
call delete_wopp_children(7);