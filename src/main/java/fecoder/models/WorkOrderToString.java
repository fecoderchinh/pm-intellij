package fecoder.models;

public class WorkOrderToString {

    private int id;
    private String name;
    private String lotNumber;
    private String purchaseOrder;
    private String year;
    private String customerId;
    private String sendDate;
    private String shippingDate;
    private String destination;
    private String note;
    private String order_date;

    public WorkOrderToString() {}

    public WorkOrderToString(int id, String name, String lotNumber, String purchaseOrder, String year, String customerId, String sendDate, String shippingDate, String destination, String note, String order_date) {
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
        this.order_date = order_date;
    }

    public int getId() { return this.id; }
    public void setId(int value) { this.id = value; }

    public String getName() { return this.name; }
    public void setName(String value) { this.name = value; }

    public String getLotNumber() { return this.lotNumber; }
    public void setLotNumber(String value) { this.lotNumber = value; }

    public String getPurchaseOrder() { return this.purchaseOrder; }
    public void setPurchaseOrder(String value) { this.purchaseOrder = value; }

    public String getYear() { return this.year; }
    public void setYear(String value) { this.year = value; }

    public String getCustomerId() { return this.customerId; }
    public void setCustomerId(String value) { this.customerId = value; }

    public String getSendDate() { return this.sendDate; }
    public void setSendDate(String value) { this.sendDate = value; }

    public String getShippingDate() { return this.shippingDate; }
    public void setShippingDate(String value) { this.shippingDate = value; }

    public String getDestination() { return this.destination; }
    public void setDestination(String value) { this.destination = value; }

    public String getNote() { return this.note; }
    public void setNote(String value) { this.note = value; }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
