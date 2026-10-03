package jp.daisukeohno.shippinginspection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 検品結果レポート(テキスト形式)を生成するクラス。
 *
 * ★テストカバレッジ検証用: このクラスにもユニットテストは用意していない。
 */
public class ReportGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    public String generate(BatchProcessor.BatchSummary summary, LocalDate inspectionDate) {
        StringBuilder sb = new StringBuilder();

        sb.append("==== 出荷検品レポート ====\n");
        sb.append("検品日: ").append(inspectionDate.format(DATE_FORMAT)).append("\n");
        sb.append("検査総数: ").append(summary.getTotalCount()).append("件\n");
        sb.append("合格: ").append(summary.getPassCount()).append("件\n");
        sb.append("警告: ").append(summary.getWarnCount()).append("件\n");
        sb.append("不合格: ").append(summary.getFailCount()).append("件\n");

        if (summary.getFailCount() > 0) {
            sb.append("\n不合格ロット一覧:\n");
            for (String lotNumber : summary.getFailedLotNumbers()) {
                sb.append("  - ").append(lotNumber).append("\n");
            }
        }

        sb.append("\n判定: ").append(determineOverallJudgement(summary)).append("\n");

        return sb.toString();
    }

    private String determineOverallJudgement(BatchProcessor.BatchSummary summary) {
        if (summary.getTotalCount() == 0) {
            return "検査対象なし";
        }

        double failRate = (double) summary.getFailCount() / summary.getTotalCount();

        if (failRate == 0) {
            return "出荷可";
        } else if (failRate < 0.01) {
            return "出荷可(要経過観察)";
        } else if (failRate < 0.05) {
            return "出荷保留(再検査推奨)";
        } else {
            return "出荷停止";
        }
    }
}
