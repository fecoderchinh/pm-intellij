package fecoder.models;

public class PackagingOwner {
    private int id;
    private int product_id;
    private int packaging_id;
    private int size_id;
    private float pack_qty;
    private String note;

    public PackagingOwner() {}

    public PackagingOwner(int id, int product_id, int packaging_id, int size_id, float pack_qty, String note) {
        this.id = id;
        this.product_id = product_id;
        this.packaging_id = packaging_id;
        this.size_id = size_id;
        this.pack_qty = pack_qty;
        this.note = note;
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public int getProduct_id() { return this.product_id; }
    public void setProduct_id(int id) { this.product_id = id; }

    public int getPackaging_id() { return this.packaging_id; }
    public void setPackaging_id(int id) { this.packaging_id = id; }

    public int getSize_id() { return this.size_id; }
    public void setSize_id(int id) { this.size_id = id; }

    public float getPack_qty() { return this.pack_qty; }
    public void setPack_qty(float id) { this.pack_qty = id; }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
