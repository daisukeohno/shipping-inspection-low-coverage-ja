package jp.daisukeohno.shippinginspection;

import jp.daisukeohno.shippinginspection.model.InspectionRecord;

/**
 * 検品ルールの判定ロジック。
 *
 * ★テストカバレッジ検証用: 分岐の多いビジネスロジックだが、
 *   このクラスに対するユニットテストは一切用意していない。
 */
public class InspectionRuleEngine {

    private static final double WEIGHT_TOLERANCE_RATIO = 0.02;   // 重量許容誤差 ±2%
    private static final double LENGTH_TOLERANCE_RATIO = 0.015;  // 寸法許容誤差 ±1.5%
    private static final double MAX_DEFECT_RATIO = 0.01;         // 不良率許容上限 1%

    public InspectionResult evaluate(InspectionRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("検品レコードがnullです");
        }

        if (record.isSurfaceDefectFound()) {
            return InspectionResult.fail("外観不良が検出されました");
        }

        if (!isWeightWithinTolerance(record)) {
            return InspectionResult.fail(String.format(
                    "重量が許容範囲外です(実測:%.2fg 基準:%.2fg)",
                    record.getWeightGrams(), record.getExpectedWeightGrams()));
        }

        if (!isLengthWithinTolerance(record)) {
            return InspectionResult.fail(String.format(
                    "寸法が許容範囲外です(実測:%.2fmm 基準:%.2fmm)",
                    record.getLengthMm(), record.getExpectedLengthMm()));
        }

        if (record.getSampleCount() <= 0) {
            return InspectionResult.fail("サンプル検査数が不正です");
        }

        double defectRatio = (double) record.getDefectCount() / record.getSampleCount();
        if (defectRatio > MAX_DEFECT_RATIO) {
            return InspectionResult.fail(String.format(
                    "サンプル不良率が許容範囲を超えています(%.2f%%)", defectRatio * 100));
        }

        if (defectRatio > 0) {
            return InspectionResult.warn(String.format(
                    "軽微な不良が検出されましたが許容範囲内です(%.2f%%)", defectRatio * 100));
        }

        return InspectionResult.pass("すべての検品項目に合格しました");
    }

    private boolean isWeightWithinTolerance(InspectionRecord record) {
        double expected = record.getExpectedWeightGrams();
        double actual = record.getWeightGrams();
        if (expected <= 0) {
            return false;
        }
        double diffRatio = Math.abs(actual - expected) / expected;
        return diffRatio <= WEIGHT_TOLERANCE_RATIO;
    }

    private boolean isLengthWithinTolerance(InspectionRecord record) {
        double expected = record.getExpectedLengthMm();
        double actual = record.getLengthMm();
        if (expected <= 0) {
            return false;
        }
        double diffRatio = Math.abs(actual - expected) / expected;
        return diffRatio <= LENGTH_TOLERANCE_RATIO;
    }

    /**
     * 検品結果(合格/警告/不合格)を表す内部クラス。
     */
    public static class InspectionResult {
        public enum Status { PASS, WARN, FAIL }

        private final Status status;
        private final String reason;

        private InspectionResult(Status status, String reason) {
            this.status = status;
            this.reason = reason;
        }

        public static InspectionResult pass(String reason) {
            return new InspectionResult(Status.PASS, reason);
        }

        public static InspectionResult warn(String reason) {
            return new InspectionResult(Status.WARN, reason);
        }

        public static InspectionResult fail(String reason) {
            return new InspectionResult(Status.FAIL, reason);
        }

        public Status getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }
    }
}
