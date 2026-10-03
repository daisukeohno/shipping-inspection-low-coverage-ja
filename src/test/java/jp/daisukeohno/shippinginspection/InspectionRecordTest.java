package jp.daisukeohno.shippinginspection;

import jp.daisukeohno.shippinginspection.model.InspectionRecord;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * ★テストカバレッジ検証用: データモデルのgetter/setterのみを確認する
 *   最小限のテスト。ビジネスロジック(InspectionRuleEngine / BatchProcessor /
 *   ReportGenerator)に対するテストは意図的に用意していない。
 */
public class InspectionRecordTest {

    @Test
    public void ロット番号を設定して取得できる() {
        InspectionRecord record = new InspectionRecord();
        record.setLotNumber("LOT-20261004-001");

        assertEquals("LOT-20261004-001", record.getLotNumber());
    }
}
