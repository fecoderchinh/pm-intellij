package fecoder.models;

public class Product {
    private int id;
    private String name;
    private String description;
    private String specification;
    private String note;

    public Product(){}

    public Product( int id, String name, String description, String specification, String note ){
        this.id = id;
        this.name = name;
        this.description = description;
        this.specification = specification;
        this.note = note;
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getSpecification() { return this.specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public String getNote() { return this.note; }
    public void  setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return this.getName();
    }
}
