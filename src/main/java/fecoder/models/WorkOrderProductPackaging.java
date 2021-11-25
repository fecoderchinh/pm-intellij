package fecoder.models;

public class WorkOrderProductPackaging {
    int id;
    int wop_id;
    int work_order_id;
    int product_id;
    int packaging_id;
    float work_order_qty;
    float stock;
    float actual_qty;
    float residual_qty;
    String printed;
    int ship_address;

    public WorkOrderProductPackaging() {}

    public int getWop_id() {
        return wop_id;
    }

    public void setWop_id(int wop_id) {
        this.wop_id = wop_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWork_order_id() {
        return work_order_id;
    }

    public void setWork_order_id(int work_order_id) {
        this.work_order_id = work_order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getPackaging_id() {
        return packaging_id;
    }

    public void setPackaging_id(int packaging_id) {
        this.packaging_id = packaging_id;
    }

    public float getWork_order_qty() {
        return work_order_qty;
    }

    public void setWork_order_qty(float work_order_qty) {
        this.work_order_qty = work_order_qty;
    }

    public float getStock() {
        return stock;
    }

    public void setStock(float stock) {
        this.stock = stock;
    }

    public float getActual_qty() {
        return actual_qty;
    }

    public void setActual_qty(float actual_qty) {
        this.actual_qty = actual_qty;
    }

    public float getResidual_qty() {
        return residual_qty;
    }

    public void setResidual_qty(float residual_qty) {
        this.residual_qty = residual_qty;
    }

    public String getPrinted() {
        return printed;
    }

    public void setPrinted(String printed) {
        this.printed = printed;
    }

    public int getShip_address() {
        return ship_address;
    }

    public void setShip_address(int ship_address) {
        this.ship_address = ship_address;
    }

    public WorkOrderProductPackaging(
            int id,
            int wop_id,
            int work_order_id,
            int product_id,
            int packaging_id,
            float work_order_qty,
            float stock,
            float actual_qty,
            float residual_qty,
            String printed,
            int ship_address
    ) {
        this.id = id;
        this.wop_id = wop_id;
        this.work_order_id = work_order_id;
        this.product_id = product_id;
        this.packaging_id = packaging_id;
        this.work_order_qty = work_order_qty;
        this.stock = stock;
        this.actual_qty = actual_qty;
        this.residual_qty = residual_qty;
        this.printed = printed;
        this.ship_address = ship_address;
    }
}
