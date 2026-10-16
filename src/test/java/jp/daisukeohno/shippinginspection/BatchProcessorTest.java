package jp.daisukeohno.shippinginspection;

import jp.daisukeohno.shippinginspection.model.InspectionRecord;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * BatchProcessor.process() のバッチ集計フローに対するテスト。
 *
 * 実物の InspectionRuleEngine を使い、PASS/WARN/FAIL となるレコードを
 * フィクスチャとして与えて集計結果を検証する。
 *
 * 補足: process() 内 switch の default 分岐(IllegalStateException)は、
 *   InspectionResult.Status が PASS/WARN/FAIL の3値 enum であり
 *   evaluate() は必ずそのいずれかを返すため到達不能。よってテスト対象外とする。
 */
public class BatchProcessorTest {

    private BatchProcessor processor;

    @Before
    public void setUp() {
        processor = new BatchProcessor(new InspectionRuleEngine());
    }

    @Test
    public void null入力の場合はゼロ集計を返す() {
        BatchProcessor.BatchSummary summary = processor.process(null);

        assertZeroSummary(summary);
    }

    @Test
    public void 空リストの場合はゼロ集計を返す() {
        BatchProcessor.BatchSummary summary = processor.process(new ArrayList<>());

        assertZeroSummary(summary);
    }

    @Test
    public void PASS_WARN_FAILが混在する場合は各件数と不合格ロット番号を正しく集計する() {
        List<InspectionRecord> records = Arrays.asList(
                passRecord("LOT-001"),
                failRecordBySurfaceDefect("LOT-002"),
                warnRecord("LOT-003"),
                passRecord("LOT-004"),
                failRecordByWeight("LOT-005"),
                warnRecord("LOT-006"),
                passRecord("LOT-007"));

        BatchProcessor.BatchSummary summary = processor.process(records);

        assertEquals(7, summary.getTotalCount());
        assertEquals(3, summary.getPassCount());
        assertEquals(2, summary.getWarnCount());
        assertEquals(2, summary.getFailCount());
        assertEquals(Arrays.asList("LOT-002", "LOT-005"), summary.getFailedLotNumbers());
    }

    @Test
    public void 全件PASSの場合は合格件数のみ加算され不合格ロットは空() {
        BatchProcessor.BatchSummary summary = processor.process(
                Arrays.asList(passRecord("LOT-101"), passRecord("LOT-102")));

        assertEquals(2, summary.getTotalCount());
        assertEquals(2, summary.getPassCount());
        assertEquals(0, summary.getWarnCount());
        assertEquals(0, summary.getFailCount());
        assertTrue(summary.getFailedLotNumbers().isEmpty());
    }

    @Test
    public void WARNのみの場合は警告件数に加算され合格や不合格には加算されない() {
        BatchProcessor.BatchSummary summary = processor.process(
                Collections.singletonList(warnRecord("LOT-201")));

        assertEquals(1, summary.getTotalCount());
        assertEquals(0, summary.getPassCount());
        assertEquals(1, summary.getWarnCount());
        assertEquals(0, summary.getFailCount());
        assertTrue(summary.getFailedLotNumbers().isEmpty());
    }

    @Test
    public void FAILのみの場合は不合格件数とロット番号が入力順に集計される() {
        BatchProcessor.BatchSummary summary = processor.process(Arrays.asList(
                failRecordByWeight("LOT-302"),
                failRecordBySurfaceDefect("LOT-301")));

        assertEquals(2, summary.getTotalCount());
        assertEquals(0, summary.getPassCount());
        assertEquals(0, summary.getWarnCount());
        assertEquals(2, summary.getFailCount());
        assertEquals(Arrays.asList("LOT-302", "LOT-301"), summary.getFailedLotNumbers());
    }

    private static void assertZeroSummary(BatchProcessor.BatchSummary summary) {
        assertNotNull(summary);
        assertEquals(0, summary.getTotalCount());
        assertEquals(0, summary.getPassCount());
        assertEquals(0, summary.getWarnCount());
        assertEquals(0, summary.getFailCount());
        assertNotNull(summary.getFailedLotNumbers());
        assertTrue(summary.getFailedLotNumbers().isEmpty());
    }

    /** 重量・寸法が基準どおり、不良0件 → PASS */
    private static InspectionRecord passRecord(String lotNumber) {
        return new InspectionRecord(lotNumber, "P-100", 100.0, 100.0, 50.0, 50.0, false, 200, 0);
    }

    /** 不良率 0.5%(1/200)で許容上限1%以内 → WARN */
    private static InspectionRecord warnRecord(String lotNumber) {
        return new InspectionRecord(lotNumber, "P-100", 100.0, 100.0, 50.0, 50.0, false, 200, 1);
    }

    /** 外観不良あり → FAIL */
    private static InspectionRecord failRecordBySurfaceDefect(String lotNumber) {
        return new InspectionRecord(lotNumber, "P-100", 100.0, 100.0, 50.0, 50.0, true, 200, 0);
    }

    /** 重量誤差 5%(許容±2%超過) → FAIL */
    private static InspectionRecord failRecordByWeight(String lotNumber) {
        return new InspectionRecord(lotNumber, "P-100", 105.0, 100.0, 50.0, 50.0, false, 200, 0);
    }
}
