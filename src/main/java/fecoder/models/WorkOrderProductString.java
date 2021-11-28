package fecoder.models;

public class WorkOrderProductString {
    int id;
    String workOrderName;
    String productName;
    String productOrdinalNumber;
    float productQuantity;
    String productNote;
    String orderTimes;

    public WorkOrderProductString() {}

    public WorkOrderProductString(
            int id,
            String workOrderName,
            String productName,
            String productOrdinalNumber,
            float productQuantity,
            String productNote,
            String orderTimes
    ) {
        this.id = id;
        this.workOrderName = workOrderName;
        this.productName = productName;
        this.productOrdinalNumber = productOrdinalNumber;
        this.productQuantity = productQuantity;
        this.productNote = productNote;
        this.orderTimes = orderTimes;
    }

    public String getWorkOrderName() {
        return workOrderName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWorkOrderName(String workOrderName) {
        this.workOrderName = workOrderName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductOrdinalNumber() {
        return productOrdinalNumber;
    }

    public void setProductOrdinalNumber(String productOrdinalNumber) {
        this.productOrdinalNumber = productOrdinalNumber;
    }

    public float getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(float productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductNote() {
        return productNote;
    }

    public void setProductNote(String productNote) {
        this.productNote = productNote;
    }

    public String getOrderTimes() {
        return orderTimes;
    }

    public void setOrderTimes(String orderTimes) {
        this.orderTimes = orderTimes;
    }
}
