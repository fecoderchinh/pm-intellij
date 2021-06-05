package fecoder.models;

public class DimensionType {

    private int id;
    private String name;
    private String unit;

    public DimensionType() {}

    public DimensionType(int id, String name, String unit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return this.unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
