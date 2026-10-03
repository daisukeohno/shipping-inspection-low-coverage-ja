# 出荷検品バッチ (shipping-inspection-low-coverage-ja)

出荷前の検品業務を模した、レガシーJava(ColdFusion由来の検品システムをJava移行した想定)
アプリケーションのデモです。

> **注意**: 本リポジトリはテストカバレッジ計測/スキャン(Devin Security Swarm等での
> カバレッジ分析)の検証を目的として、**意図的にテストをほとんど書いていない**状態の
> サンプルコードです。

## 想定シナリオ

出荷前の製品ロットに対して、複数の検品ルール(外観・寸法・重量・ロット番号整合性など)
を適用し、合否判定とレポート出力を行うバッチ処理。ビジネスロジックの分量は多いが、
移行プロジェクトの期限に追われてテストが後回しになった、という想定のレガシーコード。

## 構成

```
src/main/java/jp/daisukeohno/shippinginspection/
├── model/
│   └── InspectionRecord.java     // 検品1件分のデータモデル
├── InspectionRuleEngine.java     // 検品ルール判定ロジック(分岐多数、未テスト)
├── BatchProcessor.java           // 検品バッチの一連の処理フロー(未テスト)
└── ReportGenerator.java          // 検品結果レポートの生成(未テスト)
src/test/java/jp/daisukeohno/shippinginspection/
└── InspectionRecordTest.java     // getter/setterのみをテストする最小限のテスト
```

## カバレッジの状態

- `InspectionRecord`(単純なデータモデル)のみ、簡単なテストを1件だけ用意
- `InspectionRuleEngine` / `BatchProcessor` / `ReportGenerator` は
  **ビジネスロジックの中核にも関わらずテストが一切存在しない**
- JaCoCoを導入済みなので `mvn test` 実行後に `target/site/jacoco/index.html` で
  カバレッジレポートを確認可能(ライン/分岐カバレッジともに低い値が出る想定)

## ビルド・カバレッジ計測

```bash
mvn clean test
open target/site/jacoco/index.html
```
