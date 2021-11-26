delimiter #
create procedure insert_wopp_children
(
	in t_wop_id bigint,
	in t_work_order_id bigint,
	in t_ordinal_num varchar(100),
	in t_product_id bigint,
	in t_qty float,
	in t_note longtext,
	in t_ship_address bigint
)
begin
	insert into work_order_product (id, work_order_id, ordinal_num, product_id, qty, note)
	values (t_wop_id, t_work_order_id, t_ordinal_num, t_product_id, t_qty, "");

	insert into work_order_product_packaging(wop_id, work_order_id, product_id, packaging_id, work_order_qty, stock, actual_qty, residual_qty, ship_address)
	select t_wop_id, t_work_order_id, t_product_id, pps.packaging_id, pps.pack_qty *t_qty,0,0,0, t_ship_address
	from packaging_product_size pps, work_order_product wop
	where pps.product_id = t_product_id and wop.id = t_wop_id;
end #

delimiter;
 drop PROCEDURE insert_wopp_children;
call insert_wopp_children(6, 2, 2.0, 2, 350, "", 1 );