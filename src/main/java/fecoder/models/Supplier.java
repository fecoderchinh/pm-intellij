package fecoder.models;

public class Supplier {
    private int id;
    private String name;
    private String address;
    private String email;
    private String deputy;
    private String phone;
    private String fax;
    private String code;

    public Supplier(){}

    public Supplier(int id, String name, String address, String email, String deputy, String phone, String fax, String code) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.deputy = deputy;
        this.phone = phone;
        this.fax = fax;
        this.code = code;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDeputy() { return deputy; }
    public void setDeputy(String deputy) { this.deputy = deputy; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    @Override
    public String toString() {
        return this.getCode();
    }
}
