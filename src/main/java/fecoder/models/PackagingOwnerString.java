package fecoder.models;

public class PackagingOwnerString {

    private int id;
    private String productName;
    private String size;
    private String packagingName;
    private int pack_qty;

    public PackagingOwnerString(){}

    public PackagingOwnerString(int id, String productName, String size, String packagingName, int pack_qty){
        this.id = id;
        this.productName = productName;
        this.size = size;
        this.packagingName = packagingName;
        this.pack_qty = pack_qty;
    }

    public int getId() { return this.id; }
    public void setId(int value) { this.id = value; }

    public String getProductName() { return this.productName; }
    public void setProductName(String value) { this.productName = value; }

    public String getSize() { return this.size; }
    public void setSize(String value) { this.size = value; }

    public String getPackagingName() { return this.packagingName; }
    public void setPackagingName(String value) { this.packagingName = value; }

    public int getPack_qty() { return this.pack_qty; }
    public void setPack_qty(int value) { this.pack_qty = value; }
}
