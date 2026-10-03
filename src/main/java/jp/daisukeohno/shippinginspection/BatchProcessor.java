package jp.daisukeohno.shippinginspection;

import jp.daisukeohno.shippinginspection.model.InspectionRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 検品バッチの一連の処理フロー(対象ロットの読み込み→判定→集計)。
 *
 * ★テストカバレッジ検証用: このクラスにもユニットテストは用意していない。
 */
public class BatchProcessor {

    private final InspectionRuleEngine ruleEngine;

    public BatchProcessor(InspectionRuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public BatchSummary process(List<InspectionRecord> records) {
        if (records == null || records.isEmpty()) {
            return new BatchSummary(0, 0, 0, 0, new ArrayList<>());
        }

        int passCount = 0;
        int warnCount = 0;
        int failCount = 0;
        List<String> failedLotNumbers = new ArrayList<>();

        for (InspectionRecord record : records) {
            InspectionRuleEngine.InspectionResult result = ruleEngine.evaluate(record);

            switch (result.getStatus()) {
                case PASS:
                    passCount++;
                    break;
                case WARN:
                    warnCount++;
                    break;
                case FAIL:
                    failCount++;
                    failedLotNumbers.add(record.getLotNumber());
                    break;
                default:
                    throw new IllegalStateException("未知の検品ステータスです: " + result.getStatus());
            }
        }

        return new BatchSummary(records.size(), passCount, warnCount, failCount, failedLotNumbers);
    }

    public double calculatePassRate(BatchSummary summary) {
        if (summary.getTotalCount() == 0) {
            return 0.0;
        }
        return (double) summary.getPassCount() / summary.getTotalCount() * 100.0;
    }

    public boolean shouldHaltShipment(BatchSummary summary, double failRateThresholdPercent) {
        if (summary.getTotalCount() == 0) {
            return false;
        }
        double failRate = (double) summary.getFailCount() / summary.getTotalCount() * 100.0;
        return failRate > failRateThresholdPercent;
    }

    /**
     * バッチ処理結果の集計を表すクラス。
     */
    public static class BatchSummary {
        private final int totalCount;
        private final int passCount;
        private final int warnCount;
        private final int failCount;
        private final List<String> failedLotNumbers;

        public BatchSummary(int totalCount, int passCount, int warnCount, int failCount,
                             List<String> failedLotNumbers) {
            this.totalCount = totalCount;
            this.passCount = passCount;
            this.warnCount = warnCount;
            this.failCount = failCount;
            this.failedLotNumbers = failedLotNumbers;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int getPassCount() {
            return passCount;
        }

        public int getWarnCount() {
            return warnCount;
        }

        public int getFailCount() {
            return failCount;
        }

        public List<String> getFailedLotNumbers() {
            return failedLotNumbers;
        }
    }
}
