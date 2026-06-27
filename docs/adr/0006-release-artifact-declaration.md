# 0006. Release 配布物の宣言（mcRelease）

- Status: Accepted
- Date: 2026-06-09
- Supersedes: [ADR 0004](./0004-core-platform-adapter-split.md) の Release 配布 subproject 特定に関する記述

## Context

[ADR 0001](./0001-monorepo-and-release-via-github.md) で tag push → GitHub Release による JAR 配布を決めた。[ADR 0004](./0004-core-platform-adapter-split.md) 時点では、配布 subproject を **`:{plugin}:bukkit`** と想定していた。

その後 **metrics-exporter** が `core + bukkit + paper` となり、Release 対象は `:paper` になった。Release workflow に plugin 名の `if/else` が入り、次の懸念が出た。

| 懸念 | 概要 |
| --- | --- |
| **workflow 肥大** | プラグイン追加のたびに GHA を編集 |
| **runtime 増加** | Paper / Folia 等、1 プラグインから複数 JAR を出す可能性 |
| **推測ビルド** | `java` / `java-library` の有無で Release 先を当てると、複数 fat JAR で破綻 |

Release の **タグ形式**（`{plugin-dir}-v{semver}`）は [ADR 0001](./0001-monorepo-and-release-via-github.md) のまま維持する。変えるのは **どの subproject をビルドして asset に載せるか** の決め方。

タグと asset の対応について、次を検討した。

| 案 | 概要 |
| --- | --- |
| **A. 1 タグ = 1 プラグイン版、複数 JAR** | `metrics-exporter-v0.2.0` に Paper / Folia JAR を同時添付 |
| **B. タグに runtime を含める** | `metrics-exporter-paper-v0.2.0` のように 1 タグ 1 JAR |

案 B は asset と tag が 1:1 で単純だが、semver が runtime 単位に分裂し、同一機能リリースで tag が増える。運用側（Ansible 等）は `jar_name` で pin できるため、**案 A** を採用する。

## Decision

**案 A + Gradle 宣言** を採用する。

1. 配布 subproject は各 `build.gradle.kts` で **`vc.maximum.mc.release` convention plugin** の `mcRelease { pluginId.set("...") }` を書いて宣言する。`runtime` は subproject ディレクトリ名（`bukkit`, `paper`, `folia` 等）をデフォルトとする。
2. root の **`stageReleaseArtifacts -PreleasePlugin=<plugin-dir>`** が、宣言済み subproject の JAR をすべてビルドし、`build/release/<plugin-dir>/` に集約する。ローカル限定で `-PreleaseRuntime=` により 1 runtime だけビルドしてもよい。
3. **GitHub Actions（Release workflow）は plugin 名の分岐を書かない。** tag から parse した plugin 名で上記 Gradle タスクを 1 回呼び、`build/release/<plugin-dir>/*.jar` を upload する。
4. **1 tag に載る JAR は、その plugin の `mcRelease` 宣言 subproject すべて**（将来 Paper + Folia 等）。

```
buildSrc/                  — McReleasePlugin, McPluginShadowPlugin (conventions)
login-notify/bukkit/       — mcRelease + shadow-jar
metrics-exporter/paper/    — mcRelease + shadow-jar（Prometheus を relocate）
.github/workflows/release.yml — stageReleaseArtifacts のみ（if/else なし）
```

配布 subproject は **`vc.maximum.mc.shadow-jar`**（[Gradle Shadow](https://gradleup.com/shadow/)）を適用する。`shadowJar` が runtime 依存と自前 subproject を同梱する。Maven 依存（Prometheus 等）は **relocate** し、他プラグインとの classpath 衝突を避ける（Spigot / Maven Shade と同型の定石）。

手順の詳細は [release.md](../how-to/release.md)。

## Consequences

### Positive

- プラグイン / runtime 追加時に **workflow を触らない**
- 1 タグで同一バージョンの複数 runtime JAR を配布できる
- 配布対象が Gradle 宣言で grep 可能

### Negative

- `buildSrc` と convention plugin の分だけ build 設定が増える
- 1 Release に複数 JAR があるため、利用者は **ファイル名**（`MaximumMetricsExporter-Paper.jar` 等）で取り違えないこと

### Follow-up

- 2 個目以降のプラグイン追加時、[how-to/add-new-plugin.md](../how-to/add-new-plugin.md) に `mcRelease` 宣言をテンプレート化
- Folia 等の adapter 追加時は subproject + `mcRelease` / `mcPluginShadow` 宣言のみ（tag / workflow 変更なし）
- 新規 Maven 依存を同梱するときは `mcPluginShadow { relocation(...) }` を追加（plugin 固有 prefix へ relocate）
