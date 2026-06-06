# 0001. モノレポ化と GitHub Release による成果物配布

- Status: Accepted
- Date: 2026-06-06

## Context

Paper / Bukkit 向けプラグインを複数開発する前提。**MaximumLoginNotify**（`login-notify/`）を起点に、Gradle・CI・Paper API バージョンを共通化したい。

成果物の扱いについて、次の選択肢を検討した。

| 案 | 概要 |
| --- | --- |
| **A. JAR を git 管理** | ビルド成果物を commit する |
| **B. GitHub Release** | ソースのみ git。JAR は tag push で CI が Release に upload |

案 A だと:

- バイナリ diff が PR を汚す
- ビルド環境差で JAR がぶれる
- リリース履歴が git log と Release で二重管理になりやすい

プラグインは **1 ディレクトリ = 1 プラグイン** のモノレポにしたい（[ADR 0004](./0004-core-platform-adapter-split.md)）。

## Decision

**案 B** を採用する。

1. **mc-plugins** を Gradle マルチプロジェクトのモノレポとし、プラグインソースはここに置く。
2. **ビルド成果物（JAR）は git 管理しない。** タグ push で GitHub Actions が Release に JAR を upload する。
3. 利用者は **GitHub Release から JAR を取得**する（[release.md](../how-to/release.md)）。

タグ形式: `{plugin-dir}-v{semver}`（例: `login-notify-v1.0.0`）。

## Consequences

### Positive

- リリース履歴が GitHub Release に集約される
- モノレポで Gradle・CI・Paper API バージョンを一元管理できる
- PR はソース diff のみ

### Negative

- Release workflow と tag 命名規則の運用が必要
- ローカル `./gradlew jar` と Release asset は別物なので、配布は tag 経由に統一する

### Follow-up

- 2 個目のプラグイン追加時に [how-to/add-new-plugin.md](../how-to/add-new-plugin.md) を追加（未作成）
