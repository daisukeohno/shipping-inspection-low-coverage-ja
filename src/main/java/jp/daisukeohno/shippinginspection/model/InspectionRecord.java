package jp.daisukeohno.shippinginspection.model;

/**
 * 検品対象の1ロット分の情報を保持するデータモデル。
 */
public class InspectionRecord {

    private String lotNumber;        // ロット番号
    private String productCode;      // 製品コード
    private double weightGrams;      // 実測重量(g)
    private double expectedWeightGrams; // 基準重量(g)
    private double lengthMm;         // 実測寸法(mm)
    private double expectedLengthMm; // 基準寸法(mm)
    private boolean surfaceDefectFound; // 外観不良の有無
    private int sampleCount;         // サンプル検査数
    private int defectCount;         // 不良品数

    public InspectionRecord() {
    }

    public InspectionRecord(String lotNumber, String productCode, double weightGrams,
                             double expectedWeightGrams, double lengthMm, double expectedLengthMm,
                             boolean surfaceDefectFound, int sampleCount, int defectCount) {
        this.lotNumber = lotNumber;
        this.productCode = productCode;
        this.weightGrams = weightGrams;
        this.expectedWeightGrams = expectedWeightGrams;
        this.lengthMm = lengthMm;
        this.expectedLengthMm = expectedLengthMm;
        this.surfaceDefectFound = surfaceDefectFound;
        this.sampleCount = sampleCount;
        this.defectCount = defectCount;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public double getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(double weightGrams) {
        this.weightGrams = weightGrams;
    }

    public double getExpectedWeightGrams() {
        return expectedWeightGrams;
    }

    public void setExpectedWeightGrams(double expectedWeightGrams) {
        this.expectedWeightGrams = expectedWeightGrams;
    }

    public double getLengthMm() {
        return lengthMm;
    }

    public void setLengthMm(double lengthMm) {
        this.lengthMm = lengthMm;
    }

    public double getExpectedLengthMm() {
        return expectedLengthMm;
    }

    public void setExpectedLengthMm(double expectedLengthMm) {
        this.expectedLengthMm = expectedLengthMm;
    }

    public boolean isSurfaceDefectFound() {
        return surfaceDefectFound;
    }

    public void setSurfaceDefectFound(boolean surfaceDefectFound) {
        this.surfaceDefectFound = surfaceDefectFound;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public int getDefectCount() {
        return defectCount;
    }

    public void setDefectCount(int defectCount) {
        this.defectCount = defectCount;
    }
}
