package fecoder.models;

public class OrderBySupllier {
    int woID;
    String woName;
    String pName;
    String pSpecs;
    String pDimension;
    String pUnit;
    float pDesireQuantity;
    float pTotal;
    float pStock;
    float pResidualQuantity;
    String sCode;
    String sName;
    String sAddress;
    String sDeputy;
    String sPhone;
    String sFax;

    public OrderBySupllier() {}

    public OrderBySupllier(
            int woID,
            String woName,
            String pName,
            String pSpecs,
            String pDimension,
            String pUnit,
            float pDesireQuantity,
            float pTotal,
            float pStock,
            float pResidualQuantity,
            String sCode,
            String sName,
            String sAddress,
            String sDeputy,
            String sPhone,
            String sFax
    ) {
        this.woID = woID;
        this.woName = woName;
        this.pName = pName;
        this.pSpecs = pSpecs;
        this.pDimension = pDimension;
        this.pUnit = pUnit;
        this.pDesireQuantity = pDesireQuantity;
        this.pTotal = pTotal;
        this.pStock = pStock;
        this.pResidualQuantity = pResidualQuantity;
        this.sCode = sCode;
        this.sName = sName;
        this.sAddress = sAddress;
        this.sDeputy = sDeputy;
        this.sPhone = sPhone;
        this.sFax = sFax;
    }

    public int getWoID() {
        return woID;
    }

    public void setWoID(int woID) {
        this.woID = woID;
    }

    public String getWoName() {
        return woName;
    }

    public void setWoName(String woName) {
        this.woName = woName;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpSpecs() {
        return pSpecs;
    }

    public void setpSpecs(String pSpecs) {
        this.pSpecs = pSpecs;
    }

    public String getpDimension() {
        return pDimension;
    }

    public void setpDimension(String pDimension) {
        this.pDimension = pDimension;
    }

    public String getpUnit() {
        return pUnit;
    }

    public void setpUnit(String pUnit) {
        this.pUnit = pUnit;
    }

    public float getpDesireQuantity() {
        return pDesireQuantity;
    }

    public void setpDesireQuantity(float pDesireQuantity) {
        this.pDesireQuantity = pDesireQuantity;
    }

    public float getpTotal() {
        return pTotal;
    }

    public void setpTotal(float pTotal) {
        this.pTotal = pTotal;
    }

    public float getpStock() {
        return pStock;
    }

    public void setpStock(float pStock) {
        this.pStock = pStock;
    }

    public float getpResidualQuantity() {
        return pResidualQuantity;
    }

    public void setpResidualQuantity(float pResidualQuantity) {
        this.pResidualQuantity = pResidualQuantity;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public String getsDeputy() {
        return sDeputy;
    }

    public void setsDeputy(String sDeputy) {
        this.sDeputy = sDeputy;
    }

    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public String getsFax() {
        return sFax;
    }

    public void setsFax(String sFax) {
        this.sFax = sFax;
    }
}
