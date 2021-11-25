package fecoder.models;

public class PackagingOwnerString {

    private int id;
    private String productName;
    private String size;
    private String packagingName;
    private float pack_qty;
    private String unit;
    private String note;

    public PackagingOwnerString(){}

    public PackagingOwnerString(int id, String productName, String size, String packagingName, float pack_qty, String unit, String note){
        this.id = id;
        this.productName = productName;
        this.size = size;
        this.packagingName = packagingName;
        this.pack_qty = pack_qty;
        this.unit = unit;
        this.note = note;
    }

    public int getId() { return this.id; }
    public void setId(int value) { this.id = value; }

    public String getProductName() { return this.productName; }
    public void setProductName(String value) { this.productName = value; }

    public String getSize() { return this.size; }
    public void setSize(String value) { this.size = value; }

    public String getPackagingName() { return this.packagingName; }
    public void setPackagingName(String value) { this.packagingName = value; }

    public float getPack_qty() { return this.pack_qty; }
    public void setPack_qty(float value) { this.pack_qty = value; }

    public String getUnit() { return this.unit; }
    public void setUnit(String value) { this.unit = value; }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
