# 0003. 本リポジトリは自前開発プラグインのみを扱う

- Status: Accepted
- Date: 2026-06-06

## Context

Paper / Bukkit プラグインは **JAR を `plugins/` に置くだけで JVM 上で実行される**。第三者製 JAR の入手経路・ビルド再現性・更新レビューは、一般的な OSS より不透明になりやすい。

mc-plugins の **スコープ** を決める: この repo で何を開発し、何を配布するか。

| 対象 | 本 ADR の扱い |
| --- | --- |
| **本 repo で開発するプラグイン** | 対象。ソース・CI・Release tag で管理 |
| **第三者製 Paper/Bukkit プラグイン** | 本 repo のスコープ外（ここでは開発・配布しない） |

## Decision

1. **mc-plugins に載せるプラグインは自前開発のみ**（first-party only）。
2. **第三者製プラグインのソースや JAR を本 repo に置かない。** 必要な機能は自前実装 or fork して subproject 化する。
3. **配布は GitHub Release の JAR asset のみ**（[ADR 0001](./0001-monorepo-and-release-via-github.md)）。Release 以外のバイナリを repo に commit しない。
4. **例外**（第三者製を取り込む場合）は新規 ADR で理由・リスク・保守方針を記録する。
5. **品質ゲート**: CI テスト、pre-commit gitleaks、Conventional Commits、Release tag の semver 管理。

## Consequences

### Positive

- repo の中身が **説明可能なソースと Release 履歴**に限定される
- 機能追加が PR / CI / Release のフローに乗る
- サードパーティ JAR の「ついでに置く」が構造的に起きにくい

### Negative

- 既存の便利プラグインをそのまま bundling できない
- 必要な機能は **自前実装 or fork 保守** コストがかかる

### Follow-up

- 2 個目以降のプラグイン追加時も [how-to/add-new-plugin.md](../how-to/add-new-plugin.md)（未作成）で手順を明文化
