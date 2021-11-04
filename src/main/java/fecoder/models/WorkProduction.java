package fecoder.models;

public class WorkProduction {
    String ordinalNumbers;
    String workOrderName;
    String productName;
    String packagingName;
    String packagingSpecification;
    String packagingDimension;
    String packagingSuplier;
    String packagingCode;
    String unit;
    String printStatus;
    int packQuantity;
    float workOrderQuantity;
    float Stock;
    float actualQuantity;
    float residualQuantity;
    float totalResidualQuantity;
    String noteProduct;

    public WorkProduction() {}

    public String getOrdinalNumbers() {
        return ordinalNumbers;
    }

    public void setOrdinalNumbers(String ordinalNumbers) {
        this.ordinalNumbers = ordinalNumbers;
    }

    public String getWorkOrderName() {
        return workOrderName;
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

    public String getPackagingName() {
        return packagingName;
    }

    public void setPackagingName(String packagingName) {
        this.packagingName = packagingName;
    }

    public String getPackagingSpecification() {
        return packagingSpecification;
    }

    public void setPackagingSpecification(String packagingSpecification) {
        this.packagingSpecification = packagingSpecification;
    }

    public String getPackagingDimension() {
        return packagingDimension;
    }

    public void setPackagingDimension(String packagingDimension) {
        this.packagingDimension = packagingDimension;
    }

    public String getPackagingSuplier() {
        return packagingSuplier;
    }

    public void setPackagingSuplier(String packagingSuplier) {
        this.packagingSuplier = packagingSuplier;
    }

    public String getPackagingCode() {
        return packagingCode;
    }

    public void setPackagingCode(String packagingCode) {
        this.packagingCode = packagingCode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    public int getPackQuantity() {
        return packQuantity;
    }

    public void setPackQuantity(int packQuantity) {
        this.packQuantity = packQuantity;
    }

    public float getWorkOrderQuantity() {
        return workOrderQuantity;
    }

    public void setWorkOrderQuantity(float workOrderQuantity) {
        this.workOrderQuantity = workOrderQuantity;
    }

    public float getStock() {
        return Stock;
    }

    public void setStock(float stock) {
        Stock = stock;
    }

    public float getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(float actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public float getResidualQuantity() {
        return residualQuantity;
    }

    public void setResidualQuantity(float residualQuantity) {
        this.residualQuantity = residualQuantity;
    }

    public float getTotalResidualQuantity() {
        return totalResidualQuantity;
    }

    public void setTotalResidualQuantity(float totalResidualQuantity) {
        this.totalResidualQuantity = totalResidualQuantity;
    }

    public String getNoteProduct() {
        return noteProduct;
    }

    public void setNoteProduct(String noteProduct) {
        this.noteProduct = noteProduct;
    }

    public WorkProduction(
            String ordinalNumbers,
            String workOrderName,
            String productName,
            String packagingName,
            String packagingSpecification,
            String packagingDimension,
            String packagingSuplier,
            String packagingCode,
            String unit,
            String printStatus,
            int packQuantity,
            float workOrderQuantity,
            float Stock,
            float actualQuantity,
            float residualQuantity,
            float totalResidualQuantity,
            String noteProduct
    ) {
        this.ordinalNumbers = ordinalNumbers;
        this.workOrderName = workOrderName;
        this.productName = productName;
        this.packagingName = packagingName;
        this.packagingSpecification = packagingSpecification;
        this.packagingDimension = packagingDimension;
        this.packagingSuplier = packagingSuplier;
        this.packagingCode = packagingCode;
        this.unit = unit;
        this.printStatus = printStatus;
        this.packQuantity = packQuantity;
        this.workOrderQuantity = workOrderQuantity;
        this.Stock = Stock;
        this.actualQuantity = actualQuantity;
        this.residualQuantity = residualQuantity;
        this.totalResidualQuantity = totalResidualQuantity;
        this.noteProduct = noteProduct;
    }
}
