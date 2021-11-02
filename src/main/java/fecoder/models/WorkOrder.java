package fecoder.models;

public class WorkOrder {

    private int id;
    private String name;
    private String lotNumber;
    private String purchaseOrder;
    private int year;
    private int customerId;
    private String sendDate;
    private String shippingDate;
    private String destination;
    private String note;

    public WorkOrder() {}

    public WorkOrder(int id, String name, String lotNumber, String purchaseOrder, int year, int customerId, String sendDate, String shippingDate, String destination, String note) {
        this.id = id;
        this.name = name;
        this.lotNumber = lotNumber;
        this.purchaseOrder = purchaseOrder;
        this.year = year;
        this.customerId = customerId;
        this.sendDate = sendDate;
        this.shippingDate = shippingDate;
        this.destination = destination;
        this.note = note;
    }

    public int getId() { return this.id; }
    public void setId(int value) { this.id = value; }

    public String getName() { return this.name; }
    public void setName(String value) { this.name = value; }

    public String getLotNumber() { return this.lotNumber; }
    public void setLotNumber(String value) { this.lotNumber = value; }

    public String getPurchaseOrder() { return this.purchaseOrder; }
    public void setPurchaseOrder(String value) { this.purchaseOrder = value; }

    public int getYear() { return this.year; }
    public void setYear(int value) { this.year = value; }

    public int getCustomerId() { return this.customerId; }
    public void setCustomerId(int value) { this.customerId = value; }

    public String getSendDate() { return this.sendDate; }
    public void setSendDate(String value) { this.sendDate = value; }

    public String getShippingDate() { return this.shippingDate; }
    public void setShippingDate(String value) { this.shippingDate = value; }

    public String getDestination() { return this.destination; }
    public void setDestination(String value) { this.destination = value; }

    public String getNote() { return this.note; }
    public void setNote(String value) { this.note = value; }

    @Override
    public String toString() {
        return this.getName();
    }
}
