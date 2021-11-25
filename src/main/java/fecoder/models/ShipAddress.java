package fecoder.models;

public class ShipAddress {
    int id;
    String name;
    String address;
    String code_address;
    String stocker;
    String stocker_phone;

    public ShipAddress(){}
    public ShipAddress(
            int id,
            String name,
            String address,
            String code_address,
            String stocker,
            String stocker_phone
    ){
        this.id = id;
        this.name = name;
        this.address = address;
        this.code_address = code_address;
        this.stocker = stocker;
        this.stocker_phone = stocker_phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode_address() {
        return code_address;
    }

    public void setCode_address(String code_address) {
        this.code_address = code_address;
    }

    public String getStocker() {
        return stocker;
    }

    public void setStocker(String stocker) {
        this.stocker = stocker;
    }

    public String getStocker_phone() {
        return stocker_phone;
    }

    public void setStocker_phone(String stocker_phone) {
        this.stocker_phone = stocker_phone;
    }

    @Override
    public String toString() {
        return this.code_address;
    }
}
