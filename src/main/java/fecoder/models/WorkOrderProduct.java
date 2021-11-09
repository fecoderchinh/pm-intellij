package fecoder.models;

public class WorkOrderProduct {
    int id;
    int work_order_id;
    float ordinal_num;
    int product_id;
    float qty;
    String note;

    public WorkOrderProduct() {}

    public WorkOrderProduct(
            int id,
            int work_order_id,
            float ordinal_num,
            int product_id,
            float qty,
            String note
    ){
        this.id = id;
        this.work_order_id = work_order_id;
        this.ordinal_num = ordinal_num;
        this.product_id = product_id;
        this.qty = qty;
        this.note = note;
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

    public float getOrdinal_num() {
        return ordinal_num;
    }

    public void setOrdinal_num(float ordinal_num) {
        this.ordinal_num = ordinal_num;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
