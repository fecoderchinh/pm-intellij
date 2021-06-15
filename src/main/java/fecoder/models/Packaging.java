package fecoder.models;

public class Packaging {
    private int id;
    private int suplier;
    private int type;
    private int minimum_order;
    private boolean stamped;
    private boolean main;
    private String name;
    private String specifications;
    private String dimension;
    private String code;
    private String note;
    private float price;

    public Packaging(){}

    public Packaging(int id,
                     String name,
                     String specifications,
                     String dimension,
                     int suplier,
                     int type,
                     int minimum_order,
                     boolean stamped,
                     String code,
                     boolean main,
                     String note,
                     float price){
        this.id = id;
        this.suplier = suplier;
        this.type = type;
        this.minimum_order = minimum_order;
        this.stamped = stamped;
        this.main = main;
        this.name = name;
        this.specifications = specifications;
        this.dimension = dimension;
        this.code = code;
        this.note = note;
        this.price = price;
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public int getSuplier() { return this.suplier; }
    public void setSuplier(int suplier) { this.suplier = suplier; }

    public int getType() { return this.type; }
    public void setType(int type) { this.type = type; }

    public int getMinimum_order() { return this.minimum_order; }
    public void setMinimum_order(int minimum_order) { this.minimum_order = minimum_order; }

    public boolean isStamped() { return this.stamped; }
    public void setStamped(boolean stamped) { this.stamped = stamped; }

    public boolean isMain() { return this.main; }
    public void setMain(boolean main) { this.main = main; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getSpecifications() { return this.specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }

    public String getDimension() { return this.dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }

    public String getCode() { return this.code; }
    public void setCode(String code) { this.code = code; }

    public String getNote() { return this.note; }
    public void setNote(String note) { this.note = note; }

    public float getPrice() { return this.price; }
    public void setPrice(float price) { this.price = price; }
}
