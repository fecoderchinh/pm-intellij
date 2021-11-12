package fecoder.models;

public class Order {
    int rowNumber;
    String packagingName;
    String specs;
    String dimension;
    String unit;
    float total;
    float totalResidualQuantity;

    public Order() {}

    public Order(
            int rowNumber,
            String packagingName,
            String specs,
            String dimension,
            String unit,
            float total,
            float totalResidualQuantity
    ) {
        this.rowNumber = rowNumber;
        this.packagingName = packagingName;
        this.specs = specs;
        this.dimension = dimension;
        this.unit = unit;
        this.total = total;
        this.totalResidualQuantity = totalResidualQuantity;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getPackagingName() {
        return packagingName;
    }

    public void setPackagingName(String packagingName) {
        this.packagingName = packagingName;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getTotalResidualQuantity() {
        return totalResidualQuantity;
    }

    public void setTotalResidualQuantity(float totalResidualQuantity) {
        this.totalResidualQuantity = totalResidualQuantity;
    }
}
