package fecoder.models;

public class Customer {
    private int id;
    private String name;
    private String note;

    public Customer(){}

    public Customer(int id, String name, String note) {
        this.id = id;
        this.name = name;
        this.note = note;
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getNote() { return this.note; }
    public void setNote(String note) { this.note = note; }
}
